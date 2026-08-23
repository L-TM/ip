public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return "T" + FILE_FIELD_SEPARATOR + getStatusValue() + FILE_FIELD_SEPARATOR
                + getDescription();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
