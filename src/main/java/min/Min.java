package min;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import min.command.Command;
import min.command.Parser;
import min.exception.MinException;
import min.storage.Storage;
import min.task.Task;
import min.task.TaskList;
import min.ui.Ui;

/** Runs the Min chatbot. */
public class Min {
    private static final String INVALID_SAVED_DEADLINE_DATE_MESSAGE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";

    /**
     * Runs the chatbot and handles user commands.
     *
     * @param args Command-line arguments, which Min does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage();
        TaskList tasks;
        try {
            tasks = loadTasks(storage);
        } catch (MinException e) {
            ui.showError(e.getMessage());
            return;
        } catch (IOException e) {
            ui.showError("Unable to load tasks.");
            return;
        }

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                if (!executeCommand(command, parser, tasks, storage, ui)) {
                    break;
                }
            } catch (MinException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Unable to save tasks.");
            }
        }
    }

    /**
     * Loads saved tasks and reports incompatible deadline dates.
     *
     * @param storage The storage used to load tasks.
     * @return The loaded tasks.
     * @throws IOException If the saved task data cannot be read.
     * @throws MinException If a saved deadline date is invalid.
     */
    private static TaskList loadTasks(Storage storage) throws IOException, MinException {
        try {
            return new TaskList(storage.load());
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_SAVED_DEADLINE_DATE_MESSAGE);
        }
    }

    /**
     * Adds a task, saves the updated list, and displays its confirmation.
     *
     * @param tasks The task list to update.
     * @param task The task to add.
     * @param storage The storage used to save the task list.
     * @param ui The user interface used to show the confirmation.
     * @throws IOException If the task list cannot be saved.
     */
    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws IOException {
        tasks.addTask(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Executes one command and reports whether Min should continue running.
     *
     * @param input The command entered by the user.
     * @param parser The parser used to interpret the command.
     * @param tasks The task list to update or display.
     * @param storage The storage used to save task changes.
     * @param ui The user interface used to display results.
     * @return Whether Min should continue running.
     * @throws MinException If the command input is invalid.
     * @throws IOException If the task list cannot be saved.
     */
    static boolean executeCommand(String input, Parser parser, TaskList tasks,
            Storage storage, Ui ui) throws MinException, IOException {
        Command command = parser.parseCommand(input);
        switch (command) {
            case BYE:
                ui.showGoodbye();
                return false;
            case LIST:
                ui.showTaskList(tasks.showAllTasks());
                break;
            case FIND:
                ui.showMatchingTasks(tasks.findTasks(parser.parseFindKeyword(input)));
                break;
            case MARK:
                Task markedTask = tasks.markTask(parser.parseTaskIndex(
                        input, command, tasks.getDisplayedTaskCount()));
                storage.save(tasks.getTasks());
                ui.showTaskMarked(markedTask);
                break;
            case UNMARK:
                Task unmarkedTask = tasks.unmarkTask(
                        parser.parseTaskIndex(input, command, tasks.getDisplayedTaskCount()));
                storage.save(tasks.getTasks());
                ui.showTaskUnmarked(unmarkedTask);
                break;
            case DELETE:
                int taskIndex = parser.parseTaskIndex(
                        input, command, tasks.getDisplayedTaskCount());
                Task deletedTask = tasks.deleteTask(taskIndex);
                storage.save(tasks.getTasks());
                ui.showTaskDeleted(deletedTask, tasks.size());
                break;
            case TODO:
                addTask(tasks, parser.parseTodo(input), storage, ui);
                break;
            case DEADLINE:
                addTask(tasks, parser.parseDeadline(input), storage, ui);
                break;
            case EVENT:
                addTask(tasks, parser.parseEvent(input), storage, ui);
                break;
        }
        return true;
    }
}
