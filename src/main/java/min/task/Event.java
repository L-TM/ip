package min.task;

public class Event extends Task {

    protected String startTime;
    protected String endTime;

    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toFileString() {
        return "E" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription() + FILE_FIELD_SEPARATOR + startTime + FILE_FIELD_SEPARATOR + endTime;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), startTime, endTime);
    }
}
