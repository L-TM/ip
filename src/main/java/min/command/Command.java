package min.command;

/** Represents a command recognized by Min. */
public enum Command {
    BYE("bye", false),
    LIST("list", false),
    FIND("find", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    private final String word;
    private final boolean acceptsArguments;

    Command(String word, boolean acceptsArguments) {
        this.word = word;
        this.acceptsArguments = acceptsArguments;
    }

    /** Returns the word used to enter this command. */
    public String getWord() {
        return this.word;
    }

    /**
     * Checks whether the input begins with this command's valid command word.
     *
     * @param input The command entered by the user.
     * @return Whether the input matches this command.
     */
    public boolean matches(String input) {
        if (!this.acceptsArguments) {
            return input.equals(this.word);
        }
        return input.equals(this.word) || input.startsWith(this.word + " ");
    }
}
