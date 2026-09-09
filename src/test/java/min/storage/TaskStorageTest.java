package min.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import min.task.Deadline;
import min.task.Event;
import min.task.Task;
import min.task.Todo;

class TaskStorageTest {
    private Path dataFile;
    private TaskStorage storage;

    @BeforeEach
    void setUp(@TempDir Path tempDirectory) {
        dataFile = tempDirectory.resolve("data/min.txt");
        storage = new TaskStorage(dataFile);
    }

    @Test
    void load_missingDataFile_returnsEmptyList() throws IOException {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_mixedTasks_preservesTaskDataAndCompletionStatus() throws IOException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        Event event = new Event("meeting", "2pm", "4pm");
        todo.markAsDone();
        event.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2026-08-28",
                "E | 1 | meeting | 2pm | 4pm"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }

    @Test
    void save_existingData_replacesPreviousContents() throws IOException {
        storage.save(List.of(new Todo("old task"), new Todo("another old task")));

        storage.save(List.of(new Todo("replacement task")));

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("T | 0 | replacement task", loadedTasks.get(0).toFileString());
    }

    @Test
    void load_unknownTaskType_throwsIllegalArgumentException() throws IOException {
        writeDataFile("X | 0 | unknown task");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Unknown task type.", exception.getMessage());
    }

    @Test
    void load_invalidDeadlineDate_throwsDateTimeParseException() throws IOException {
        writeDataFile("D | 0 | submit report | 2026-02-30");

        assertThrows(DateTimeParseException.class, storage::load);
    }

    private void writeDataFile(String contents) throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, contents);
    }
}
