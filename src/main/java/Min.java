import java.util.Scanner;

public class Min {
    private static final int SEPARATOR_LENGTH = 60;
    private static final int MAX_TASKS = 100;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);

    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark ";
    private static final String UNMARK_COMMAND = "unmark ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
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
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(SEPARATOR);
            } else if (command.startsWith(MARK_COMMAND)) {
                // Handle mark command
                String[] parts = command.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                tasks[taskNumber - 1].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(" " + tasks[taskNumber - 1]);
                System.out.println(SEPARATOR);
            } else if (command.startsWith(UNMARK_COMMAND)) {
                String[] parts = command.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                tasks[taskNumber - 1].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(" " + tasks[taskNumber - 1]);
                System.out.println(SEPARATOR);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
                System.out.println(SEPARATOR);
            }
        }
    }
}
