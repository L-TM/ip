/** Represents a task that can be marked as complete and saved to storage. */
public abstract class Task {
    protected static final String FILE_FIELD_SEPARATOR = " | ";

    private String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task is marked as complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns this task's completion status in the format used for persistent storage. */
    protected String getStatusValue() {
        return isDone ? "1" : "0";
    }

    /** Returns this task in the format used for persistent storage. */
    public abstract String toFileString();

    /** Returns this task in a format suitable for display. */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}
