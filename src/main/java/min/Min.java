package min;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import min.command.Command;
import min.command.Parser;
import min.exception.MinException;
import min.list.NoteList;
import min.list.TaskList;
import min.note.Note;
import min.storage.NoteStorage;
import min.storage.TaskStorage;
import min.task.Task;

/** Processes commands and manages Min's task data. */
public class Min {
    private static final String INVALID_SAVED_DEADLINE_DATE_MESSAGE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";
    private static final String TASK_LIST_HEADING = "Here are the tasks in your list:";
    private static final String NOTE_LIST_HEADING = "Here are the notes in your list:";
    private static final String MATCHING_TASK_HEADING =
            "Here are the matching tasks in your list:";
    private static final String MATCHING_NOTE_HEADING =
            "Here are the matching notes in your list:";
    private static final String SECTION_SEPARATOR = "\n\n";
    private static final String WELCOME_MESSAGE =
            "Hello! I'm Min.\nWhat can I do for you?";

    private final Parser parser;
    private final TaskStorage taskStorage;
    private final NoteStorage noteStorage;
    private final TaskList tasks;
    private final NoteList notes;

    /**
     * Creates Min and loads its saved tasks and notes.
     *
     * @throws IOException If the saved data cannot be read.
     * @throws MinException If the saved task data is invalid.
     */
    public Min() throws IOException, MinException {
        this.parser = new Parser();
        this.taskStorage = new TaskStorage();
        this.noteStorage = new NoteStorage();
        this.tasks = loadTasks(this.taskStorage);
        this.notes = new NoteList(this.noteStorage.load());
    }

    /**
     * Creates Min using the supplied dependencies.
     *
     * @param parser The parser used to interpret commands.
     * @param tasks The task list managed by Min.
     * @param taskStorage The storage used to save task data.
     * @param notes The note list managed by Min.
     * @param noteStorage The storage used to save note data.
     */
    Min(Parser parser, TaskList tasks, TaskStorage taskStorage,
            NoteList notes, NoteStorage noteStorage) {
        assert parser != null : "Parser must not be null.";
        assert tasks != null : "Task list must not be null.";
        assert taskStorage != null : "Task storage must not be null.";
        assert notes != null : "Note list must not be null.";
        assert noteStorage != null : "Note storage must not be null.";

        this.parser = parser;
        this.tasks = tasks;
        this.taskStorage = taskStorage;
        this.notes = notes;
        this.noteStorage = noteStorage;
    }

    /**
     * Loads saved tasks and reports incompatible deadline dates.
     *
     * @param storage The storage used to load tasks.
     * @return The loaded tasks.
     * @throws IOException If the saved task data cannot be read.
     * @throws MinException If a saved deadline date is invalid.
     */
    private static TaskList loadTasks(TaskStorage storage) throws IOException, MinException {
        try {
            return new TaskList(storage.load());
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_SAVED_DEADLINE_DATE_MESSAGE);
        }
    }

    /**
     * Adds a task, saves the updated list, and returns its confirmation.
     *
     * @param task The task to add.
     * @return The confirmation message.
     * @throws IOException If the task list cannot be saved.
     */
    private String addTask(Task task) throws IOException {
        this.tasks.addTask(task);
        this.taskStorage.save(this.tasks.getTasks());

        return " Got it. I've added this task:\n"
                + "   " + task + "\n"
                + " Now you have " + this.tasks.size() + " tasks in the list.";
    }

    /** Returns Min's welcome message. */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Returns whether the input requests that Min exit.
     *
     * @param input The command entered by the user.
     * @return Whether the input is the exit command.
     */
    public boolean isExitCommand(String input) {
        return Command.BYE.matches(input.trim());
    }

    /**
     * Processes user input and returns a response suitable for any user interface.
     *
     * @param input The command entered by the user.
     * @return Min's response.
     */
    public String getResponse(String input) {
        try {
            return executeCommand(input.trim());
        } catch (MinException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Unable to save tasks.";
        }
    }

    /**
     * Formats items as a numbered list.
     *
     * @param heading The heading shown before the items.
     * @param displayedItems The items to include.
     * @return The formatted item list.
     */
    private String formatItemList(String heading, List<?> displayedItems) {
        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < displayedItems.size(); i++) {
            response.append("\n ")
                    .append(i + 1)
                    .append(".")
                    .append(displayedItems.get(i));
        }

        return response.toString();
    }

    /**
     * Finds matching tasks and notes and returns them as two numbered lists.
     *
     * @param input The find command entered by the user.
     * @return The formatted matching-task list followed by the matching-note list.
     * @throws MinException If the find keyword is missing.
     */
    private String findItems(String input) throws MinException {
        String keyword = this.parser.parseFindKeyword(input);

        return formatItemList(MATCHING_TASK_HEADING, this.tasks.showMatchingTasks(keyword))
                + SECTION_SEPARATOR
                + formatItemList(MATCHING_NOTE_HEADING, this.notes.showMatchingNotes(keyword));
    }

    /** Returns every task followed by every note, each as a numbered list. */
    private String listAllItems() {
        return formatItemList(TASK_LIST_HEADING, this.tasks.showAllTasks())
                + SECTION_SEPARATOR
                + formatItemList(NOTE_LIST_HEADING, this.notes.showAllNotes());
    }

    /**
     * Adds a note, saves the updated list, and returns its confirmation.
     *
     * @param note The note to add.
     * @return The confirmation message.
     * @throws IOException If the note list cannot be saved.
     */
    private String addNote(Note note) throws IOException {
        this.notes.addNote(note);
        this.noteStorage.save(this.notes.getNotes());

        return " Got it. I've added this note:\n"
                + "   " + note + "\n"
                + " Now you have " + this.notes.size() + " notes in the list.";
    }

    /**
     * Deletes the selected note, saves the updated list, and returns its confirmation.
     *
     * @param input The delete-note command entered by the user.
     * @return The confirmation message.
     * @throws MinException If the note number is invalid.
     * @throws IOException If the note list cannot be saved.
     */
    private String deleteNote(String input) throws MinException, IOException {
        int noteIndex = this.parser.parseNoteIndex(input, this.notes.getDisplayedNoteCount());
        Note deletedNote = this.notes.deleteNote(noteIndex);
        this.noteStorage.save(this.notes.getNotes());

        return " Got it. I've removed this note:\n"
                + "   " + deletedNote + "\n"
                + " Now you have " + this.notes.size() + " notes in the list.";
    }

    /**
     * Marks the selected task, saves the task list, and returns its confirmation.
     *
     * @param input The mark command entered by the user.
     * @return The confirmation message.
     * @throws MinException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String markTask(String input) throws MinException, IOException {
        int taskIndex = this.parser.parseTaskIndex(
                input, Command.MARK, this.tasks.getDisplayedTaskCount());
        Task markedTask = this.tasks.markTask(taskIndex);
        this.taskStorage.save(this.tasks.getTasks());

        return "Nice! I've marked this task as done:\n"
                + "   " + markedTask;
    }

    /**
     * Unmarks the selected task, saves the task list, and returns its confirmation.
     *
     * @param input The unmark command entered by the user.
     * @return The confirmation message.
     * @throws MinException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String unmarkTask(String input) throws MinException, IOException {
        int taskIndex = this.parser.parseTaskIndex(
                input, Command.UNMARK, this.tasks.getDisplayedTaskCount());
        Task unmarkedTask = this.tasks.unmarkTask(taskIndex);
        this.taskStorage.save(this.tasks.getTasks());

        return "OK, I've marked this task as not done yet:\n"
                + "   " + unmarkedTask;
    }

    /**
     * Deletes the selected task, saves the task list, and returns its confirmation.
     *
     * @param input The delete command entered by the user.
     * @return The confirmation message.
     * @throws MinException If the task number is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    private String deleteTask(String input) throws MinException, IOException {
        int taskIndex = this.parser.parseTaskIndex(
                input, Command.DELETE, this.tasks.getDisplayedTaskCount());
        Task deletedTask = this.tasks.deleteTask(taskIndex);
        this.taskStorage.save(this.tasks.getTasks());

        return " Got it. I've removed this task:\n"
                + "   " + deletedTask + "\n"
                + " Now you have " + this.tasks.size() + " tasks in the list.";
    }

    /**
     * Executes one command and returns Min's response.
     *
     * @param input The command entered by the user.
     * @return Min's response.
     * @throws MinException If the command input is invalid.
     * @throws IOException If the task or note list cannot be saved.
     */
    private String executeCommand(String input) throws MinException, IOException {
        Command command = this.parser.parseCommand(input);

        assert command != null : "Parser must return a command or throw an exception.";

        switch (command) {
            case BYE:
                return " Bye. Hope to see you again soon!";
            case LIST:
                return listAllItems();
            case LISTTASKS:
                return formatItemList(TASK_LIST_HEADING, this.tasks.showAllTasks());
            case LISTNOTES:
                return formatItemList(NOTE_LIST_HEADING, this.notes.showAllNotes());
            case FIND:
                return findItems(input);
            case MARK:
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case TODO:
                return addTask(this.parser.parseTodo(input));
            case DEADLINE:
                return addTask(this.parser.parseDeadline(input));
            case EVENT:
                return addTask(this.parser.parseEvent(input));
            case NOTE:
                return addNote(this.parser.parseNote(input));
            case DELETENOTE:
                return deleteNote(input);
        }

        throw new IllegalStateException("Unhandled command: " + command);
    }
}
