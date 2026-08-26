package min.task;

public abstract class Task {
    protected static final String FILE_FIELD_SEPARATOR = " | ";

    private String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    protected String getStatusValue() {
        return isDone ? "1" : "0";
    }

    public abstract String toFileString();

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}
