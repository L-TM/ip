import java.io.IOException;
import java.time.format.DateTimeParseException;

/** Runs the Min chatbot. */
public class Min {
    private static final String INVALID_SAVED_DEADLINE_DATE_MESSAGE =
            "Unable to load tasks. Saved deadline dates must use yyyy-mm-dd.";

    // Runs the chatbot and handles user commands.
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

    // Loads saved tasks and reports incompatible deadline dates.
    private static TaskList loadTasks(Storage storage) throws IOException, MinException {
        try {
            return new TaskList(storage.load());
        } catch (DateTimeParseException e) {
            throw new MinException(INVALID_SAVED_DEADLINE_DATE_MESSAGE);
        }
    }

    // Adds a task and displays its confirmation.
    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws IOException {
        tasks.addTask(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.size());
    }

    // Executes one command and reports whether Min should continue running.
    private static boolean executeCommand(String input, Parser parser, TaskList tasks,
            Storage storage, Ui ui) throws MinException, IOException {
        Command command = parser.parseCommand(input);
        switch (command) {
        case BYE:
            ui.showGoodbye();
            return false;
        case LIST:
            ui.showTaskList(tasks.getTasks());
            break;
        case MARK:
            Task markedTask = tasks.markTask(parser.parseTaskIndex(input, command, tasks.size()));
            storage.save(tasks.getTasks());
            ui.showTaskMarked(markedTask);
            break;
        case UNMARK:
            Task unmarkedTask = tasks.unmarkTask(
                    parser.parseTaskIndex(input, command, tasks.size()));
            storage.save(tasks.getTasks());
            ui.showTaskUnmarked(unmarkedTask);
            break;
        case DELETE:
            int taskIndex = parser.parseTaskIndex(input, command, tasks.size());
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
