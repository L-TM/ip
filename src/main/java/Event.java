/** Represents a task that occurs between a specified start and end time. */
public class Event extends Task {

    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns this event in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "E" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription() + FILE_FIELD_SEPARATOR + from + FILE_FIELD_SEPARATOR + to;
    }

    /** Returns this event in a format suitable for display. */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }
}
