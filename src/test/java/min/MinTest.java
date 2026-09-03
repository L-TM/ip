package min;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import min.command.Parser;
import min.storage.Storage;
import min.task.Task;
import min.task.TaskList;
import min.task.Todo;

class MinTest {
    private final Parser parser = new Parser();
    private final Storage storage = new NoOpStorage();

    @Test
    void isExitCommand_byeWithSurroundingWhitespace_returnsTrue() {
        Min min = new Min(parser, new TaskList(List.of()), storage);

        assertTrue(min.isExitCommand("  bye  "));
    }

    @Test
    void isExitCommand_nonByeInputs_returnsFalse() {
        Min min = new Min(parser, new TaskList(List.of()), storage);

        assertFalse(min.isExitCommand("byebye"));
        assertFalse(min.isExitCommand("list"));
        assertFalse(min.isExitCommand("dance"));
    }

    @Test
    void getResponse_bye_returnsGoodbyeMessage() {
        Min min = new Min(parser, new TaskList(List.of()), storage);

        assertEquals(" Bye. Hope to see you again soon!", min.getResponse("bye"));
    }

    @Test
    void getResponse_findThenMark_marksSecondDisplayedMatch() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = new Min(parser, tasks, storage);

        min.getResponse("find read");
        min.getResponse("mark 2");

        assertFalse(nonMatch.isDone());
        assertTrue(secondMatch.isDone());
    }

    @Test
    void getResponse_indexOutsideFindResults_returnsRangeError() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = new Min(parser, tasks, storage);
        min.getResponse("find read");

        String response = min.getResponse("mark 3");

        assertEquals("Task number must be between 1 and 2.", response);
        assertFalse(secondMatch.isDone());
    }

    @Test
    void getResponse_listAfterFind_restoresFullListNumbering() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy milk");
        Todo secondMatch = new Todo("reread notes");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));
        Min min = new Min(parser, tasks, storage);

        min.getResponse("find read");
        min.getResponse("list");
        min.getResponse("mark 2");

        assertTrue(nonMatch.isDone());
        assertFalse(secondMatch.isDone());
    }

    private static class NoOpStorage extends Storage {
        @Override
        public void save(List<Task> tasks) {
        }
    }
}
