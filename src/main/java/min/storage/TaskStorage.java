package min.storage;

import java.nio.file.Path;
import java.time.LocalDate;

import min.task.Deadline;
import min.task.Event;
import min.task.Task;
import min.task.Todo;

/** Saves and loads Min tasks on the hard disk. */
public class TaskStorage extends Storage<Task> {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "min.txt");
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String TODO_TYPE_VALUE = "T";
    private static final String DEADLINE_TYPE_VALUE = "D";
    private static final String EVENT_TYPE_VALUE = "E";
    private static final String INCOMPLETE_STATUS_VALUE = "0";
    private static final String COMPLETE_STATUS_VALUE = "1";
    private static final String DAMAGED_RECORD_MESSAGE = "Damaged task record.";
    private static final String INVALID_STATUS_MESSAGE = "Invalid task status.";
    private static final String BLANK_DESCRIPTION_MESSAGE =
            "Task description cannot be blank.";
    private static final String BLANK_EVENT_TIME_MESSAGE = "Event times cannot be blank.";

    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_FIELD_INDEX = 3;
    private static final int EVENT_END_FIELD_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    /** Creates storage that uses Min's default task data file. */
    public TaskStorage() {
        this(DEFAULT_FILE_PATH);
    }

    TaskStorage(Path filePath) {
        super(filePath);
    }

    @Override
    protected String encode(Task task) {
        return task.toFileString();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If the line is not a valid saved task record.
     * @throws java.time.format.DateTimeParseException If a saved deadline date is invalid.
     */
    @Override
    protected Task decode(String line) {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        Task task = switch (fields[TYPE_FIELD_INDEX]) {
            case TODO_TYPE_VALUE -> {
                validateTaskFields(fields, TODO_FIELD_COUNT);
                yield new Todo(fields[DESCRIPTION_FIELD_INDEX]);
            }
            case DEADLINE_TYPE_VALUE -> {
                validateTaskFields(fields, DEADLINE_FIELD_COUNT);
                yield new Deadline(fields[DESCRIPTION_FIELD_INDEX],
                        LocalDate.parse(fields[DEADLINE_DATE_FIELD_INDEX]));
            }
            case EVENT_TYPE_VALUE -> {
                validateTaskFields(fields, EVENT_FIELD_COUNT);
                validateNonBlank(fields[EVENT_START_FIELD_INDEX], BLANK_EVENT_TIME_MESSAGE);
                validateNonBlank(fields[EVENT_END_FIELD_INDEX], BLANK_EVENT_TIME_MESSAGE);
                yield new Event(fields[DESCRIPTION_FIELD_INDEX],
                        fields[EVENT_START_FIELD_INDEX], fields[EVENT_END_FIELD_INDEX]);
            }
            default -> throw new IllegalArgumentException("Unknown task type.");
        };

        if (fields[STATUS_FIELD_INDEX].equals(COMPLETE_STATUS_VALUE)) {
            task.markAsDone();
        }
        return task;
    }

    /** Validates the fields shared by every saved task type. */
    private static void validateTaskFields(String[] fields, int expectedFieldCount) {
        validateFieldCount(fields, expectedFieldCount);
        validateStatus(fields[STATUS_FIELD_INDEX]);
        validateNonBlank(fields[DESCRIPTION_FIELD_INDEX], BLANK_DESCRIPTION_MESSAGE);
    }

    /** Validates that a saved task has exactly the expected number of fields. */
    private static void validateFieldCount(String[] fields, int expectedFieldCount) {
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException(DAMAGED_RECORD_MESSAGE);
        }
    }

    /** Validates that a saved completion status is either incomplete or complete. */
    private static void validateStatus(String status) {
        if (!status.equals(INCOMPLETE_STATUS_VALUE) && !status.equals(COMPLETE_STATUS_VALUE)) {
            throw new IllegalArgumentException(INVALID_STATUS_MESSAGE);
        }
    }

    /** Validates that a required saved text field contains non-whitespace text. */
    private static void validateNonBlank(String text, String errorMessage) {
        if (text.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
