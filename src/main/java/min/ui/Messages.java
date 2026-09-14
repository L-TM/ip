package min.ui;

/**
 * Stores the text Min shows to the user.
 *
 * <p>Collecting the wording in one place keeps Min's voice consistent and means a change
 * of phrasing touches this class alone rather than every class that talks to the user.
 * Messages that embed a task, a note, or a count are built by the methods below; the rest
 * are plain constants.
 *
 * <p>Item text is passed in as an already-formatted {@code String} so that this class does
 * not depend on the task and note packages.
 */
public final class Messages {
    // Greeting and farewell.
    public static final String WELCOME = "Hello! I'm Min.\nWhat can I do for you?";
    public static final String GOODBYE = " Bye. Hope to see you again soon!";

    // Headings shown above a numbered list of items.
    public static final String TASK_LIST_HEADING = "Here are the tasks in your list:";
    public static final String NOTE_LIST_HEADING = "Here are the notes in your list:";
    public static final String MATCHING_TASK_HEADING = "Here are the matching tasks in your list:";
    public static final String MATCHING_NOTE_HEADING = "Here are the matching notes in your list:";

    // Errors caused by invalid command input.
    public static final String INVALID_COMMAND =
            "Invalid command. Use bye, list, listtasks, listnotes, find, mark, unmark, delete, "
                    + "todo, deadline, event, note, or deletenote.";
    public static final String INVALID_TODO = "A todo needs a description. Use: todo <description>.";
    public static final String INVALID_DEADLINE =
            "Invalid deadline. Use: deadline <description> /by yyyy-mm-dd.";
    public static final String INVALID_DEADLINE_DATE = "Invalid deadline date. Use yyyy-mm-dd.";
    public static final String INVALID_EVENT =
            "Invalid event. Use: event <description> /from <time> /to <time>.";
    public static final String INVALID_FIND = "Please provide a keyword to find.";
    public static final String INVALID_NOTE = "A note needs some text. Use: note <text>.";
    public static final String INVALID_NOTE_TEXT = "A note cannot contain \" | \".";
    public static final String INVALID_TASK_TEXT = "Task details cannot contain \" | \".";

    // Errors caused by unreadable or unwritable saved data.
    public static final String SAVE_DATA_ERROR = "Unable to save data.";
    public static final String LOAD_DATA_ERROR = "Unable to load saved data.";
    public static final String INVALID_SAVED_DEADLINE_DATE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";

    // Text shown when Min cannot start.
    public static final String APP_NAME = "Min";
    public static final String STARTUP_ERROR_HEADER = "Unable to start Min";
    public static final String STARTUP_ERROR = "Unable to start Min.";
    public static final String UNEXPECTED_STARTUP_ERROR =
            "Min could not start because of an unexpected error.";

    /** Prevents instantiation of this constants holder. */
    private Messages() {
    }

    /**
     * Returns the confirmation shown after a task is added.
     *
     * @param taskText The added task as it is displayed.
     * @param taskCount The number of tasks now in the list.
     */
    public static String addedTask(String taskText, int taskCount) {
        return " Got it. I've added this task:\n"
                + "   " + taskText + "\n"
                + " Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns the confirmation shown after a note is added.
     *
     * @param noteText The added note as it is displayed.
     * @param noteCount The number of notes now in the list.
     */
    public static String addedNote(String noteText, int noteCount) {
        return " Got it. I've added this note:\n"
                + "   " + noteText + "\n"
                + " Now you have " + noteCount + " notes in the list.";
    }

    /**
     * Returns the confirmation shown after a task is marked as done.
     *
     * @param taskText The marked task as it is displayed.
     */
    public static String markedTask(String taskText) {
        return "Nice! I've marked this task as done:\n"
                + "   " + taskText;
    }

    /**
     * Returns the confirmation shown after a task is marked as not done.
     *
     * @param taskText The unmarked task as it is displayed.
     */
    public static String unmarkedTask(String taskText) {
        return "OK, I've marked this task as not done yet:\n"
                + "   " + taskText;
    }

    /**
     * Returns the confirmation shown after a task is deleted.
     *
     * @param taskText The deleted task as it is displayed.
     * @param taskCount The number of tasks left in the list.
     */
    public static String removedTask(String taskText, int taskCount) {
        return " Got it. I've removed this task:\n"
                + "   " + taskText + "\n"
                + " Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns the confirmation shown after a note is deleted.
     *
     * @param noteText The deleted note as it is displayed.
     * @param noteCount The number of notes left in the list.
     */
    public static String removedNote(String noteText, int noteCount) {
        return " Got it. I've removed this note:\n"
                + "   " + noteText + "\n"
                + " Now you have " + noteCount + " notes in the list.";
    }

    /**
     * Returns the error shown when saved task data cannot be understood.
     *
     * @param details The reason the saved data was rejected.
     */
    public static String corruptedTaskData(String details) {
        return "Unable to load tasks. Fix or delete data/min.txt. Details: " + details;
    }

    /**
     * Returns the error shown when saved note data cannot be understood.
     *
     * @param details The reason the saved data was rejected.
     */
    public static String corruptedNoteData(String details) {
        return "Unable to load notes. Fix or delete data/notes.txt. Details: " + details;
    }

    /**
     * Returns the error shown when a command needs an item number but none was given.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param action The action the number was needed for, such as "mark".
     */
    public static String missingIndex(String itemName, String action) {
        return "Please provide a " + itemName + " number to " + action + ".";
    }

    /**
     * Returns the error shown when a command needs an item but the list is empty.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param action The action that could not be performed, such as "mark".
     */
    public static String noItems(String itemName, String action) {
        return "There are no " + itemName + "s to " + action + ".";
    }

    /**
     * Returns the error shown when an item number falls outside the list.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param itemCount The number of items the number may refer to.
     */
    public static String indexOutOfRange(String itemName, int itemCount) {
        return capitalize(itemName) + " number must be between 1 and " + itemCount + ".";
    }

    /**
     * Returns the error shown when an item number is not a whole number.
     *
     * @param itemName The singular name of the item, such as "task".
     */
    public static String indexNotANumber(String itemName) {
        return "The " + itemName + " number must be a whole number.";
    }

    /** Returns the word with its first letter in upper case. */
    private static String capitalize(String word) {
        assert !word.isEmpty() : "Word to capitalize must not be empty.";

        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
