package min.ui;

import java.util.List;
import java.util.Scanner;

import min.task.Task;

/** Handles Min's console input and output. */
public class Ui {
    private static final int SEPARATOR_LENGTH = 60;
    private static final String SEPARATOR = "_".repeat(SEPARATOR_LENGTH);

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available from the user. */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /** Returns the next command entered by the user with leading and trailing whitespace removed. */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /** Prints Min's welcome message. */
    public void showWelcome() {
        String banner = " __  __ _       \n"
                + "|  \\/  (_)_ __  \n"
                + "| |\\/| | | '_ \\ \n"
                + "| |  | | | | | | |\n"
                + "|_|  |_|_|_| |_|\n";
        System.out.println(banner);
        showLine();
        System.out.println("Hello! I'm Min.");
        System.out.println("What can I do for you?");
        showLine();
    }

    /** Prints Min's standard separator. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Prints Min's goodbye message. */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Prints the current task list.
     *
     * @param tasks The tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Prints the tasks matching a search keyword.
     *
     * @param tasks The matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Prints a confirmation after marking a task as complete.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("   " + task);
        showLine();
    }

    /**
     * Prints a confirmation after marking a task as incomplete.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        showLine();
    }

    /**
     * Prints a confirmation after adding a task.
     *
     * @param task The added task.
     * @param taskCount The number of tasks currently in the list.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /**
     * Prints a confirmation after deleting a task.
     *
     * @param task The deleted task.
     * @param taskCount The number of tasks remaining in the list.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Got it. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        showLine();
    }

    /**
     * Prints an error message.
     *
     * @param message The message to display.
     */
    public void showError(String message) {
        System.out.println(" " + message);
        showLine();
    }
}
