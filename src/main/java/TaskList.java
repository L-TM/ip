import java.util.ArrayList;
import java.util.List;

/** Stores and updates Min's tasks. */
public class TaskList {
    private final List<Task> tasks;

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Marks the task at the given index as complete.
     *
     * @param index The zero-based index of the task to mark.
     * @return The marked task.
     */
    public Task markTask(int index) {
        Task task = this.tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given index as incomplete.
     *
     * @param index The zero-based index of the task to unmark.
     * @return The unmarked task.
     */
    public Task unmarkTask(int index) {
        Task task = this.tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index The zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task deleteTask(int index) {
        return this.tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return this.tasks.size();
    }

    /** Returns a read-only snapshot of the current tasks. */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }
}
