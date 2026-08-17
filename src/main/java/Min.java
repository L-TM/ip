import java.util.Scanner;

public class Min {
    private static final int SEPARATOR_LENGTH = 60;
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

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

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals(EXIT_COMMAND)) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            } else if (command.equals(LIST_COMMAND)) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(SEPARATOR);
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
                System.out.println(SEPARATOR);
            }
        }
    }
}
