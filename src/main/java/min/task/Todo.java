package min.task;

/** Represents a task without a date or time. */
public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    /** Returns this todo in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "T" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription();
    }

    /** Returns this todo in a format suitable for display. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
