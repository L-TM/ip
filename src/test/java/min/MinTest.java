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
    private final RecordingStorage storage = new RecordingStorage();

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
    void getResponse_todo_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("todo read book");

        assertEquals(" Got it. I've added this task:\n"
                + "   [T][ ] read book\n"
                + " Now you have 1 tasks in the list.", response);
        assertEquals("T | 0 | read book", tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_deadline_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("deadline submit report /by 2026-08-28");

        assertEquals(" Got it. I've added this task:\n"
                + "   [D][ ] submit report (by: Aug 28 2026)\n"
                + " Now you have 1 tasks in the list.", response);
        assertEquals("D | 0 | submit report | 2026-08-28",
                tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_event_addsTaskAndReturnsConfirmation() {
        TaskList tasks = new TaskList(List.of());
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("event meeting /from 2pm /to 4pm");

        assertEquals(" Got it. I've added this task:\n"
                + "   [E][ ] meeting (from: 2pm to: 4pm)\n"
                + " Now you have 1 tasks in the list.", response);
        assertEquals("E | 0 | meeting | 2pm | 4pm",
                tasks.getTasks().get(0).toFileString());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_mark_marksTaskAndReturnsConfirmation() {
        Todo task = new Todo("read book");
        Min min = new Min(parser, new TaskList(List.of(task)), storage);

        String response = min.getResponse("mark 1");

        assertEquals("Nice! I've marked this task as done:\n"
                + "   [T][X] read book", response);
        assertTrue(task.isDone());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_unmark_unmarksTaskAndReturnsConfirmation() {
        Todo task = new Todo("read book");
        task.markAsDone();
        Min min = new Min(parser, new TaskList(List.of(task)), storage);

        String response = min.getResponse("unmark 1");

        assertEquals("OK, I've marked this task as not done yet:\n"
                + "   [T][ ] read book", response);
        assertFalse(task.isDone());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_delete_removesTaskAndReturnsConfirmation() {
        Todo deletedTask = new Todo("read book");
        Todo remainingTask = new Todo("buy milk");
        TaskList tasks = new TaskList(List.of(deletedTask, remainingTask));
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("delete 1");

        assertEquals(" Got it. I've removed this task:\n"
                + "   [T][ ] read book\n"
                + " Now you have 1 tasks in the list.", response);
        assertEquals(List.of(remainingTask), tasks.getTasks());
        assertEquals(1, storage.getSaveCount());
    }

    @Test
    void getResponse_list_returnsNumberedTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk")));
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("list");

        assertEquals("Here are the tasks in your list:\n"
                + " 1.[T][ ] read book\n"
                + " 2.[T][ ] buy milk", response);
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    void getResponse_find_returnsNumberedMatches() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk")));
        Min min = new Min(parser, tasks, storage);

        String response = min.getResponse("find read");

        assertEquals("Here are the matching tasks in your list:\n"
                + " 1.[T][ ] read book", response);
        assertEquals(0, storage.getSaveCount());
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

    private static class RecordingStorage extends Storage {
        private int saveCount;

        @Override
        public void save(List<Task> tasks) {
            saveCount++;
        }

        private int getSaveCount() {
            return saveCount;
        }
    }
}
