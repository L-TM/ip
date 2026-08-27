package min.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import min.exception.MinException;
import min.task.Deadline;
import min.task.Event;
import min.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommand_validCommands_returnsCorrespondingCommands() throws MinException {
        assertEquals(Command.BYE, parser.parseCommand("bye"));
        assertEquals(Command.LIST, parser.parseCommand("list"));
        assertEquals(Command.FIND, parser.parseCommand("find book"));
        assertEquals(Command.MARK, parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, parser.parseCommand("delete 1"));
        assertEquals(Command.TODO, parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE,
                parser.parseCommand("deadline submit report /by 2026-08-28"));
        assertEquals(Command.EVENT,
                parser.parseCommand("event meeting /from 2pm /to 4pm"));
    }

    @Test
    void parseCommand_bareArgumentCommands_returnsCorrespondingCommands() throws MinException {
        assertEquals(Command.FIND, parser.parseCommand("find"));
        assertEquals(Command.MARK, parser.parseCommand("mark"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark"));
        assertEquals(Command.DELETE, parser.parseCommand("delete"));
        assertEquals(Command.TODO, parser.parseCommand("todo"));
        assertEquals(Command.DEADLINE, parser.parseCommand("deadline"));
        assertEquals(Command.EVENT, parser.parseCommand("event"));
    }

    @Test
    void parseCommand_invalidCommands_throwsMinException() {
        String expectedMessage = "Invalid command. Use bye, list, find, mark, unmark, "
                + "delete, todo, deadline, or event.";

        assertThrowsMinException(expectedMessage, () -> parser.parseCommand(""));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("dance"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("byebye"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("list now"));
    }

    @Test
    void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndexes() throws MinException {
        assertEquals(0, parser.parseTaskIndex("mark 1", Command.MARK, 3));
        assertEquals(2, parser.parseTaskIndex("delete 3", Command.DELETE, 3));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_throwsMinException() {
        assertThrowsMinException("Please provide a task number to mark.",
                () -> parser.parseTaskIndex("mark", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_missingNumberWithEmptyTaskList_throwsMissingNumberException() {
        assertThrowsMinException("Please provide a task number to mark.",
                () -> parser.parseTaskIndex("mark", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_nonWholeNumber_throwsMinException() {
        assertThrowsMinException("The task number must be a whole number.",
                () -> parser.parseTaskIndex("mark two", Command.MARK, 3));
        assertThrowsMinException("The task number must be a whole number.",
                () -> parser.parseTaskIndex("mark 1.5", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_decimalNumberWithEmptyTaskList_throwsWholeNumberException() {
        assertThrowsMinException("The task number must be a whole number.",
                () -> parser.parseTaskIndex("mark 1.5", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_emptyTaskList_throwsMinException() {
        assertThrowsMinException("There are no tasks to mark.",
                () -> parser.parseTaskIndex("mark 1", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_negativeNumberWithEmptyTaskList_throwsNoTasksException() {
        assertThrowsMinException("There are no tasks to mark.",
                () -> parser.parseTaskIndex("mark -1", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_negativeNumberWithNonEmptyTaskList_throwsRangeException() {
        assertThrowsMinException("Task number must be between 1 and 3.",
                () -> parser.parseTaskIndex("mark -1", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_outOfRangeTaskNumber_throwsMinException() {
        assertThrowsMinException("Task number must be between 1 and 3.",
                () -> parser.parseTaskIndex("mark 0", Command.MARK, 3));
        assertThrowsMinException("Task number must be between 1 and 3.",
                () -> parser.parseTaskIndex("mark 4", Command.MARK, 3));
    }

    @Test
    void parseTodo_validInput_returnsTodo() throws MinException {
        Todo todo = parser.parseTodo("todo   read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    void parseTodo_missingDescription_throwsMinException() {
        assertThrowsMinException("A todo needs a description. Use: todo <description>.",
                () -> parser.parseTodo("todo"));
    }

    @Test
    void parseFindKeyword_validInput_returnsKeyword() throws MinException {
        assertEquals("read book", parser.parseFindKeyword("find   read book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsMinException() {
        assertThrowsMinException("Please provide a keyword to find.",
                () -> parser.parseFindKeyword("find"));
    }

    @Test
    void parseDeadline_validInput_returnsDeadline() throws MinException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2026-08-28");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("D | 0 | submit report | 2026-08-28", deadline.toFileString());
    }

    @Test
    void parseDeadline_missingDetails_throwsMinException() {
        String expectedMessage =
                "Invalid deadline. Use: deadline <description> /by yyyy-mm-dd.";

        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline /by 2026-08-28"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    void parseDeadline_invalidDate_throwsMinException() {
        String expectedMessage = "Invalid deadline date. Use yyyy-mm-dd.";

        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by 28-08-2026"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by 2026-02-30"));
    }

    @Test
    void parseEvent_validInput_returnsEvent() throws MinException {
        Event event = parser.parseEvent("event meeting /from 2pm /to 4pm");

        assertEquals("meeting", event.getDescription());
        assertEquals("E | 0 | meeting | 2pm | 4pm", event.toFileString());
    }

    @Test
    void parseEvent_missingDetails_throwsMinException() {
        String expectedMessage =
                "Invalid event. Use: event <description> /from <time> /to <time>.";

        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from 2pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event /from 2pm /to 4pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from  /to 4pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from 2pm /to"));
    }

    private void assertThrowsMinException(String expectedMessage, Executable executable) {
        MinException exception = assertThrows(MinException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
