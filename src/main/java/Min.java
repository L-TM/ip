import java.util.Scanner;

public class Min {
    private static final int SEPARATOR_LENGTH = 60;
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);

    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark ";
    private static final String UNMARK_COMMAND = "unmark ";
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";

    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

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
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

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

            } else if (command.startsWith(MARK_COMMAND)) {
                String[] parts = command.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                tasks[taskNumber - 1].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("   " + tasks[taskNumber - 1]);
                System.out.println(SEPARATOR);

            } else if (command.startsWith(UNMARK_COMMAND)) {
                String[] parts = command.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                tasks[taskNumber - 1].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[taskNumber - 1]);
                System.out.println(SEPARATOR);

            } else if (command.startsWith(TODO_COMMAND)) {
                String description = command.substring(TODO_COMMAND.length());
                tasks[taskCount] = new Todo(description);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
                System.out.println(SEPARATOR);

            } else if (command.startsWith(DEADLINE_COMMAND)) {
                String deadlineDetails = command.substring(DEADLINE_COMMAND.length());
                int byIndex = deadlineDetails.indexOf(BY_SEPARATOR);
                String description = deadlineDetails.substring(0, byIndex);
                String by = deadlineDetails.substring(byIndex + BY_SEPARATOR.length());
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
                System.out.println(SEPARATOR);

            } else if (command.startsWith(EVENT_COMMAND)) {
                String eventDetails = command.substring(EVENT_COMMAND.length());
                int fromIndex = eventDetails.indexOf(FROM_SEPARATOR);
                String description = eventDetails.substring(0, fromIndex);
                String eventTimes = eventDetails.substring(fromIndex + FROM_SEPARATOR.length());
                int toIndex = eventTimes.indexOf(TO_SEPARATOR);
                String from = eventTimes.substring(0, toIndex);
                String to = eventTimes.substring(toIndex + TO_SEPARATOR.length());
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
                System.out.println(SEPARATOR);

            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
                System.out.println(SEPARATOR);
            }
        }
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }
}
