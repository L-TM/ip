import java.util.Scanner;

/** Runs the Min chatbot. */
public class Min {
    private static final int SEPARATOR_LENGTH = 60;
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);

    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";

    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    private static final String INVALID_EVENT_MESSAGE =
            "Invalid event. Use: event <description> /from <time> /to <time>.";
    private static final String INVALID_TODO_MESSAGE =
            "A todo needs a description. Use: todo <description>.";
    private static final String INVALID_DEADLINE_MESSAGE =
            "Invalid deadline. Use: deadline <description> /by <time>.";
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Use bye, list, mark, unmark, todo, deadline, or event.";
    private static final String TASK_LIST_FULL_MESSAGE =
            "Task list is full. You can store up to " + MAX_TASKS + " tasks.";

    // Runs the chatbot and handles user commands.
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        String banner = " __  __ _       \n"
                + "|  \\/  (_)_ __  \n"
                + "| |\\/| | | '_ \\ \n"
                + "| |  | | | | | |\n"
                + "|_|  |_|_|_| |_|\n";
        System.out.println(banner);
        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Min.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(SEPARATOR);

            try {
                if (command.equals(EXIT_COMMAND)) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(SEPARATOR);
                    break;
                } else if (command.equals(LIST_COMMAND)) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                    System.out.println(SEPARATOR);
                } else if (isCommand(command, MARK_COMMAND)) {
                    int taskNumber = getTaskNumber(command, MARK_COMMAND, taskCount);
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskNumber - 1]);
                    System.out.println(SEPARATOR);
                } else if (isCommand(command, UNMARK_COMMAND)) {
                    int taskNumber = getTaskNumber(command, UNMARK_COMMAND, taskCount);
                    tasks[taskNumber - 1].markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskNumber - 1]);
                    System.out.println(SEPARATOR);
                } else if (isCommand(command, TODO_COMMAND)) {
                    String description = command.substring(TODO_COMMAND.length()).trim();
                    if (description.isEmpty()) {
                        throw new MinException(INVALID_TODO_MESSAGE);
                    }
                    taskCount = addTask(tasks, taskCount, new Todo(description));
                } else if (isCommand(command, DEADLINE_COMMAND)) {
                    String deadlineDetails = command.substring(DEADLINE_COMMAND.length()).trim();
                    int byIndex = deadlineDetails.indexOf(BY_SEPARATOR);
                    if (byIndex == -1) {
                        throw new MinException(INVALID_DEADLINE_MESSAGE);
                    }
                    String description = deadlineDetails.substring(0, byIndex).trim();
                    String by = deadlineDetails.substring(byIndex + BY_SEPARATOR.length()).trim();
                    if (description.isEmpty() || by.isEmpty()) {
                        throw new MinException(INVALID_DEADLINE_MESSAGE);
                    }
                    taskCount = addTask(tasks, taskCount, new Deadline(description, by));
                } else if (isCommand(command, EVENT_COMMAND)) {
                    String eventDetails = command.substring(EVENT_COMMAND.length()).trim();
                    int fromIndex = eventDetails.indexOf(FROM_SEPARATOR);
                    if (fromIndex == -1) {
                        throw new MinException(INVALID_EVENT_MESSAGE);
                    }
                    String description = eventDetails.substring(0, fromIndex).trim();
                    String eventTimes = eventDetails.substring(fromIndex + FROM_SEPARATOR.length());
                    int toIndex = eventTimes.indexOf(TO_SEPARATOR);
                    if (toIndex == -1) {
                        throw new MinException(INVALID_EVENT_MESSAGE);
                    }
                    String from = eventTimes.substring(0, toIndex).trim();
                    String to = eventTimes.substring(toIndex + TO_SEPARATOR.length()).trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        throw new MinException(INVALID_EVENT_MESSAGE);
                    }
                    taskCount = addTask(tasks, taskCount, new Event(description, from, to));
                } else {
                    throw new MinException(INVALID_COMMAND_MESSAGE);
                }
            } catch (MinException e) {
                printError(e.getMessage());
            }
        }
    }

    // Adds a task, prints its confirmation, and returns the new task count.
    private static int addTask(Task[] tasks, int taskCount, Task task) throws MinException {
        ensureTaskListHasRoom(taskCount);
        tasks[taskCount] = task;
        taskCount++;
        printAddedTask(task, taskCount);
        System.out.println(SEPARATOR);
        return taskCount;
    }

    // Prints a confirmation after adding a task.
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    // Checks whether the input contains a command word with an optional argument.
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    // Gets a valid task number from a mark or unmark command.
    private static int getTaskNumber(String command, String commandWord, int taskCount)
            throws MinException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new MinException("Please provide a task number to " + commandWord + ".");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskCount == 0) {
                throw new MinException("There are no tasks to " + commandWord + ".");
            }
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new MinException("Task number must be between 1 and " + taskCount + ".");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new MinException("The task number must be a whole number.");
        }
    }

    // Checks whether another task can be stored in the task list.
    private static void ensureTaskListHasRoom(int taskCount) throws MinException {
        if (taskCount >= MAX_TASKS) {
            throw new MinException(TASK_LIST_FULL_MESSAGE);
        }
    }

    // Prints an error message followed by the standard separator.
    private static void printError(String message) {
        System.out.println(" " + message);
        System.out.println(SEPARATOR);
    }
}
