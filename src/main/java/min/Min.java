package min;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import min.command.Command;
import min.command.Parser;
import min.exception.MinException;
import min.storage.Storage;
import min.task.Task;
import min.task.TaskList;

/** Processes commands and manages Min's task data. */
public class Min {
    private static final String INVALID_SAVED_DEADLINE_DATE_MESSAGE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";
    private static final String WELCOME_MESSAGE =
            "Hello! I'm Min.\nWhat can I do for you?";

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates Min and loads its saved tasks.
     *
     * @throws IOException If the saved tasks cannot be read.
     * @throws MinException If the saved task data is invalid.
     */
    public Min() throws IOException, MinException {
        this.parser = new Parser();
        this.storage = new Storage();
        this.tasks = loadTasks(this.storage);
    }

    /**
     * Creates Min using the supplied dependencies.
     *
     * @param parser The parser used to interpret commands.
     * @param tasks The task list managed by Min.
     * @param storage The storage used to save task data.
     */
    Min(Parser parser, TaskList tasks, Storage storage) {
        this.parser = parser;
        this.tasks = tasks;
        this.storage = storage;
    }

    /**
     * Loads saved tasks and reports incompatible deadline dates.
     *
     * @param storage The storage used to load tasks.
     * @return The loaded tasks.
     * @throws IOException If the saved task data cannot be read.
     * @throws MinException If a saved deadline date is invalid.
     */
    private static TaskList loadTasks(Storage storage) throws IOException, MinException {
        try {
            return new TaskList(storage.load());
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_SAVED_DEADLINE_DATE_MESSAGE);
        }
    }

    /**
     * Adds a task, saves the updated list, and returns its confirmation.
     *
     * @param task The task to add.
     * @return The confirmation message.
     * @throws IOException If the task list cannot be saved.
     */
    private String addTask(Task task) throws IOException {
        this.tasks.addTask(task);
        this.storage.save(this.tasks.getTasks());

        return " Got it. I've added this task:\n"
                + "   " + task + "\n"
                + " Now you have " + this.tasks.size() + " tasks in the list.";
    }

    /** Returns Min's welcome message. */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Returns whether the input requests that Min exit.
     *
     * @param input The command entered by the user.
     * @return Whether the input is the exit command.
     */
    public boolean isExitCommand(String input) {
        return Command.BYE.matches(input.trim());
    }

    /**
     * Processes user input and returns a response suitable for any user interface.
     *
     * @param input The command entered by the user.
     * @return Min's response.
     */
    public String getResponse(String input) {
        try {
            return executeCommand(input.trim());
        } catch (MinException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Unable to save tasks.";
        }
    }

    /**
     * Formats tasks as a numbered list.
     *
     * @param heading The heading shown before the tasks.
     * @param displayedTasks The tasks to include.
     * @return The formatted task list.
     */
    private String formatTaskList(String heading, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append("\n ")
                    .append(i + 1)
                    .append(".")
                    .append(displayedTasks.get(i));
        }

        return response.toString();
    }

    /**
     * Executes one command and returns Min's response.
     *
     * @param input The command entered by the user.
     * @return Min's response.
     * @throws MinException If the command input is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String executeCommand(String input) throws MinException, IOException {
        Command command = this.parser.parseCommand(input);

        switch (command) {
            case BYE:
                return " Bye. Hope to see you again soon!";
            case LIST:
                return formatTaskList(
                        "Here are the tasks in your list:",
                        this.tasks.showAllTasks());
            case FIND:
                return formatTaskList(
                        "Here are the matching tasks in your list:",
                        this.tasks.findTasks(this.parser.parseFindKeyword(input)));
            case MARK:
                Task markedTask = this.tasks.markTask(this.parser.parseTaskIndex(
                        input, command, this.tasks.getDisplayedTaskCount()));
                this.storage.save(this.tasks.getTasks());
                return "Nice! I've marked this task as done:\n"
                        + "   " + markedTask;
            case UNMARK:
                Task unmarkedTask = this.tasks.unmarkTask(
                        this.parser.parseTaskIndex(
                                input, command, this.tasks.getDisplayedTaskCount()));
                this.storage.save(this.tasks.getTasks());
                return "OK, I've marked this task as not done yet:\n"
                        + "   " + unmarkedTask;
            case DELETE:
                int taskIndex = this.parser.parseTaskIndex(
                        input, command, this.tasks.getDisplayedTaskCount());
                Task deletedTask = this.tasks.deleteTask(taskIndex);
                this.storage.save(this.tasks.getTasks());
                return " Got it. I've removed this task:\n"
                        + "   " + deletedTask + "\n"
                        + " Now you have " + this.tasks.size() + " tasks in the list.";
            case TODO:
                return addTask(this.parser.parseTodo(input));
            case DEADLINE:
                return addTask(this.parser.parseDeadline(input));
            case EVENT:
                return addTask(this.parser.parseEvent(input));
        }

        throw new IllegalStateException("Unhandled command: " + command);
    }
}
