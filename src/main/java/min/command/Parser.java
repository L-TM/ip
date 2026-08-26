package min.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import min.exception.MinException;
import min.task.Deadline;
import min.task.Event;
import min.task.Todo;

// Parses and validates commands entered by the user.
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
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid command. Use bye, list, mark, unmark, delete, todo, deadline, or event.";

    // Identifies the command represented by the input.
    public Command parseCommand(String input) throws MinException {
        for (Command command : Command.values()) {
            if (command.matches(input)) {
                return command;
            }
        }
        throw new MinException(INVALID_COMMAND_MESSAGE);
    }

    // Parses a valid task number into a zero-based task index.
    public int parseTaskIndex(String input, Command command, int taskCount)
            throws MinException {
        String taskNumberText = getDetails(input, command);
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

    // Creates a todo task from the input.
    public Todo parseTodo(String input) throws MinException {
        String description = getDetails(input, Command.TODO);
        if (description.isEmpty()) {
            throw new MinException(INVALID_TODO_MESSAGE);
        }
        return new Todo(description);
    }

    // Creates a deadline task from the input.
    public Deadline parseDeadline(String input) throws MinException {
        String deadlineDetails = getDetails(input, Command.DEADLINE);
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

    // Creates an event task from the input.
    public Event parseEvent(String input) throws MinException {
        String eventDetails = getDetails(input, Command.EVENT);
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

    // Gets the text after a command word.
    private String getDetails(String input, Command command) {
        return input.substring(command.getWord().length()).trim();
    }

    // Parses a deadline date in the required ISO format.
    private LocalDate parseDeadlineDate(String dateText) throws MinException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_DEADLINE_DATE_MESSAGE);
        }
    }
}
