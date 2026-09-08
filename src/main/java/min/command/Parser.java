package min.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import min.exception.MinException;
import min.task.Deadline;
import min.task.Event;
import min.task.Todo;

/** Parses and validates commands entered by the user. */
public class Parser {
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
    private static final String INVALID_FIND_MESSAGE =
            "Please provide a keyword to find.";
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Use bye, list, find, mark, unmark, delete, todo, deadline, or event.";

    /**
     * Identifies the command represented by the input.
     *
     * @param input The command entered by the user.
     * @return The identified command.
     * @throws MinException If the input does not begin with a valid command word.
     */
    public Command parseCommand(String input) throws MinException {
        return Arrays.stream(Command.values())
                .filter(command -> command.matches(input))
                .findFirst()
                .orElseThrow(() -> new MinException(INVALID_COMMAND_MESSAGE));
    }

    /**
     * Parses a valid task number into a zero-based task index.
     *
     * @param input The command entered by the user.
     * @param command The command that requires a task number.
     * @param taskCount The number of tasks currently in the list.
     * @return The zero-based index of the requested task.
     * @throws MinException If the task number is missing, malformed, or outside the task list.
     */
    public int parseTaskIndex(String input, Command command, int taskCount)
            throws MinException {
        String taskNumberText = extractArguments(input, command);
        assert taskCount >= 0 : "Task count must not be negative.";
        assert command == Command.MARK
                || command == Command.UNMARK
                || command == Command.DELETE
                : "Only mark, unmark, and delete commands use task indexes.";

        if (taskNumberText.isEmpty()) {
            throw new MinException("Please provide a task number to " + command.getWord() + ".");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskCount == 0) {
                throw new MinException("There are no tasks to " + command.getWord() + ".");
            }
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new MinException("Task number must be between 1 and " + taskCount + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new MinException("The task number must be a whole number.");
        }
    }

    /**
     * Creates a todo task from the input.
     *
     * @param input The command entered by the user.
     * @return The created todo task.
     * @throws MinException If the todo description is missing.
     */
    public Todo parseTodo(String input) throws MinException {
        String description = extractArguments(input, Command.TODO);
        if (description.isEmpty()) {
            throw new MinException(INVALID_TODO_MESSAGE);
        }
        return new Todo(description);
    }

    /**
     * Parses the keyword used to find tasks.
     *
     * @param input The command entered by the user.
     * @return The keyword used to match task descriptions.
     * @throws MinException If no find keyword is provided.
     */
    public String parseFindKeyword(String input) throws MinException {
        String keyword = extractArguments(input, Command.FIND);
        if (keyword.isEmpty()) {
            throw new MinException(INVALID_FIND_MESSAGE);
        }
        return keyword;
    }

    /**
     * Creates a deadline task from the input.
     *
     * @param input The command entered by the user.
     * @return The created deadline task.
     * @throws MinException If the deadline details or date are invalid.
     */
    public Deadline parseDeadline(String input) throws MinException {
        String deadlineDetails = extractArguments(input, Command.DEADLINE);
        int byIndex = deadlineDetails.indexOf(BY_SEPARATOR);
        if (byIndex == -1) {
            throw new MinException(INVALID_DEADLINE_MESSAGE);
        }

        String description = deadlineDetails.substring(0, byIndex).trim();
        String byText = deadlineDetails.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new MinException(INVALID_DEADLINE_MESSAGE);
        }
        return new Deadline(description, parseDeadlineDate(byText));
    }

    /**
     * Creates an event task from the input.
     *
     * @param input The command entered by the user.
     * @return The created event task.
     * @throws MinException If the event details are invalid.
     */
    public Event parseEvent(String input) throws MinException {
        String eventDetails = extractArguments(input, Command.EVENT);
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
        return new Event(description, from, to);
    }

    private String extractArguments(String input, Command command) {
        assert input != null : "Command input must not be null.";
        assert command != null : "Command must not be null.";
        assert command.matches(input)
                : "Input must match the command whose details are being parsed.";

        return input.substring(command.getWord().length()).trim();
    }

    /**
     * Parses a deadline date in the required ISO format.
     *
     * @param dateText The date text to parse.
     * @return The parsed deadline date.
     * @throws MinException If the date text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String dateText) throws MinException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_DEADLINE_DATE_MESSAGE);
        }
    }
}
