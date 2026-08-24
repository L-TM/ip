import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Runs the Min chatbot. */
public class Min {
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    private static final String INVALID_EVENT_MESSAGE =
            "Invalid event. Use: event <description> /from <time> /to <time>.";
    private static final String INVALID_TODO_MESSAGE =
            "A todo needs a description. Use: todo <description>.";
    private static final String INVALID_DEADLINE_MESSAGE =
            "Invalid deadline. Use: deadline <description> /by yyyy-mm-dd.";
    private static final String INVALID_DEADLINE_DATE_MESSAGE =
            "Invalid deadline date. Use yyyy-mm-dd.";
    private static final String INVALID_SAVED_DEADLINE_DATE_MESSAGE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Use bye, list, mark, unmark, delete, todo, deadline, or event.";

    // Represents the command words Min accepts.
    private enum Command {
        BYE("bye"),
        LIST("list"),
        MARK("mark"),
        UNMARK("unmark"),
        DELETE("delete"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event");

        private final String word;

        Command(String word) {
            this.word = word;
        }

        private String getWord() {
            return this.word;
        }
    }

    // Runs the chatbot and handles user commands.
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage();
        TaskList tasks;
        try {
            tasks = loadTasks(storage);
        } catch (MinException e) {
            ui.showError(e.getMessage());
            return;
        } catch (IOException e) {
            ui.showError("Unable to load tasks.");
            return;
        }

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                if (command.equals(Command.BYE.getWord())) {
                    ui.showGoodbye();
                    break;
                } else if (command.equals(Command.LIST.getWord())) {
                    ui.showTaskList(tasks.getTasks());
                } else if (isCommand(command, Command.MARK)) {
                    int taskNumber = getTaskNumber(command, Command.MARK, tasks.size());
                    Task task = tasks.markTask(taskNumber - 1);
                    storage.save(tasks.getTasks());
                    ui.showTaskMarked(task);
                } else if (isCommand(command, Command.UNMARK)) {
                    int taskNumber = getTaskNumber(command, Command.UNMARK, tasks.size());
                    Task task = tasks.unmarkTask(taskNumber - 1);
                    storage.save(tasks.getTasks());
                    ui.showTaskUnmarked(task);
                } else if (isCommand(command, Command.DELETE)) {
                    int taskNumber = getTaskNumber(command, Command.DELETE, tasks.size());
                    Task removedTask = tasks.deleteTask(taskNumber - 1);
                    storage.save(tasks.getTasks());
                    ui.showTaskDeleted(removedTask, tasks.size());
                } else if (isCommand(command, Command.TODO)) {
                    String description = command.substring(Command.TODO.getWord().length()).trim();
                    if (description.isEmpty()) {
                        throw new MinException(INVALID_TODO_MESSAGE);
                    }
                    addTask(tasks, new Todo(description), storage, ui);
                } else if (isCommand(command, Command.DEADLINE)) {
                    String deadlineDetails =
                            command.substring(Command.DEADLINE.getWord().length()).trim();
                    int byIndex = deadlineDetails.indexOf(BY_SEPARATOR);
                    if (byIndex == -1) {
                        throw new MinException(INVALID_DEADLINE_MESSAGE);
                    }
                    String description = deadlineDetails.substring(0, byIndex).trim();
                    String byText =
                            deadlineDetails.substring(byIndex + BY_SEPARATOR.length()).trim();
                    if (description.isEmpty() || byText.isEmpty()) {
                        throw new MinException(INVALID_DEADLINE_MESSAGE);
                    }
                    LocalDate by = parseDeadlineDate(byText);
                    addTask(tasks, new Deadline(description, by), storage, ui);
                } else if (isCommand(command, Command.EVENT)) {
                    String eventDetails =
                            command.substring(Command.EVENT.getWord().length()).trim();
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
                    addTask(tasks, new Event(description, from, to), storage, ui);
                } else {
                    throw new MinException(INVALID_COMMAND_MESSAGE);
                }
            } catch (MinException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Unable to save tasks.");
            }
        }
    }

    // Loads saved tasks and reports incompatible deadline dates.
    private static TaskList loadTasks(Storage storage) throws IOException, MinException {
        try {
            return new TaskList(storage.load());
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_SAVED_DEADLINE_DATE_MESSAGE);
        }
    }

    // Adds a task and displays its confirmation.
    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws IOException {
        tasks.addTask(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    // Parses a deadline date in the required ISO format.
    private static LocalDate parseDeadlineDate(String dateText) throws MinException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_DEADLINE_DATE_MESSAGE);
        }
    }

    // Checks whether the input contains a command word with an optional argument.
    private static boolean isCommand(String input, Command command) {
        String commandWord = command.getWord();
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    // Gets a valid task number from a numbered task command.
    private static int getTaskNumber(String input, Command command, int taskCount)
            throws MinException {
        String commandWord = command.getWord();
        String taskNumberText = input.substring(commandWord.length()).trim();
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
}
