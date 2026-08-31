package min;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import min.command.Parser;
import min.exception.MinException;
import min.storage.Storage;
import min.task.Task;
import min.task.TaskList;
import min.task.Todo;
import min.ui.Ui;

class MinTest {
    private final Parser parser = new Parser();
    private final Storage storage = new NoOpStorage();
    private final Ui ui = new SilentUi();

    @Test
    void executeCommand_findThenMark_marksSecondDisplayedMatch()
            throws MinException, IOException {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        Min.executeCommand("find read", parser, tasks, storage, ui);
        Min.executeCommand("mark 2", parser, tasks, storage, ui);

        assertFalse(nonMatch.isDone());
        assertTrue(secondMatch.isDone());
    }

    @Test
    void executeCommand_indexOutsideFindResults_throwsRangeException()
            throws MinException, IOException {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min.executeCommand("find read", parser, tasks, storage, ui);

        MinException exception = assertThrows(MinException.class,
                () -> Min.executeCommand("mark 3", parser, tasks, storage, ui));

        assertEquals("Task number must be between 1 and 2.", exception.getMessage());
        assertFalse(secondMatch.isDone());
    }

    @Test
    void executeCommand_listAfterFind_restoresFullListNumbering()
            throws MinException, IOException {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        Min.executeCommand("find read", parser, tasks, storage, ui);
        Min.executeCommand("list", parser, tasks, storage, ui);
        Min.executeCommand("mark 2", parser, tasks, storage, ui);

        assertTrue(nonMatch.isDone());
        assertFalse(secondMatch.isDone());
    }

    private static class NoOpStorage extends Storage {
        @Override
        public void save(List<Task> tasks) {
        }
    }

    private static class SilentUi extends Ui {
        @Override
        public void showTaskList(List<Task> tasks) {
        }

        @Override
        public void showMatchingTasks(List<Task> tasks) {
        }

        @Override
        public void showTaskMarked(Task task) {
        }
    }
}
