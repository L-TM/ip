package min.task;

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
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword The text to match against task descriptions.
     * @return A read-only list of matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return List.copyOf(matchingTasks);
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
