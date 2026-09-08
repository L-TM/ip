package min.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import min.task.Deadline;
import min.task.Event;
import min.task.Task;
import min.task.Todo;

/** Saves and loads Min tasks on the hard disk. */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "min.txt");

    private final Path filePath;

    /** Creates storage that uses Min's default data file. */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Rewrites the data file with the current task list.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(filePath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
        }
    }

    /**
     * Loads all saved tasks from the data file.
     *
     * @return A read-only list of loaded tasks, or an empty list when no data file exists.
     * @throws IOException If the data file cannot be read.
     * @throws java.time.format.DateTimeParseException If a saved deadline date is invalid.
     */
    public List<Task> load() throws IOException {
        if (!Files.exists(filePath)) {
            return List.of();
        }

        return Files.readAllLines(filePath).stream()
                .map(line -> this.createTask(line))
                .toList();
    }

    /**
     * Recreates a task from one line of saved data.
     *
     * @param line A line read from the data file.
     * @return The recreated task.
     * @throws IllegalArgumentException If the line has an unknown task type.
     */
    private Task createTask(String line) {
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
