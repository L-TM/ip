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
     * @throws IllegalArgumentException If the line has an unknown task type.
     * @throws java.time.format.DateTimeParseException If a saved deadline date is invalid.
     */
    @Override
    protected Task decode(String line) {
        String[] fields = line.split(" \\| ", -1);
        Task task = switch (fields[0]) {
            case "T" -> new Todo(fields[2]);
            case "D" -> new Deadline(fields[2], LocalDate.parse(fields[3]));
            case "E" -> new Event(fields[2], fields[3], fields[4]);
            default -> throw new IllegalArgumentException("Unknown task type.");
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
