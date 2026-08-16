public class Min {
    private static final int SEPARATOR_LENGTH = 50;
    private static final String SEPARATOR = "-".repeat(SEPARATOR_LENGTH);

    public static void main(String[] args) {
        String banner = " __  __ _       \n"
                + "|  \\/  (_)_ __  \n"
                + "| |\\/| | | '_ \\ \n"
                + "| |  | | | | | |\n"
                + "|_|  |_|_|_| |_|\n";
        System.out.println(banner);
        System.out.println(SEPARATOR);
        System.out.println("Hello! I'm Min.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
        System.out.println("Bye. Hope you will have some fun!");
        System.out.println(SEPARATOR);
    }
}
