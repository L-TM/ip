package min.task;

/** Represents a task that occurs between a specified start and end time. */
public class Event extends Task {

    private final String startTime;
    private final String endTime;

    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /** Returns this event in the format used for persistent storage. */
    @Override
    public String toFileString() {
        return "E" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription() + FILE_FIELD_SEPARATOR + startTime + FILE_FIELD_SEPARATOR + endTime;
    }

    /** Returns this event in a format suitable for display. */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), startTime, endTime);
    }
}
