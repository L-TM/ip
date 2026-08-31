package min.task;

import java.util.ArrayList;
import java.util.List;

/** Stores and updates Min's tasks. */
public class TaskList {
    private final List<Task> tasks;
    private final List<Task> displayedTasks;
    private boolean isShowingAllTasks;

    /**
     * Creates a task list whose initial displayed view contains every task.
     *
     * @param tasks The initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        this.displayedTasks = new ArrayList<>(tasks);
        this.isShowingAllTasks = true;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
        if (this.isShowingAllTasks) {
            this.displayedTasks.add(task);
        }
    }

    /**
     * Sets the displayed view to tasks whose descriptions contain the given keyword.
     *
     * @param keyword The text to match against task descriptions.
     * @return A read-only list of matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        this.displayedTasks.clear();
        for (Task task : this.tasks) {
            if (task.getDescription().contains(keyword)) {
                this.displayedTasks.add(task);
            }
        }
        this.isShowingAllTasks = false;
        return List.copyOf(this.displayedTasks);
    }

    /** Restores and returns the complete task list as the displayed view. */
    public List<Task> showAllTasks() {
        this.displayedTasks.clear();
        this.displayedTasks.addAll(this.tasks);
        this.isShowingAllTasks = true;
        return List.copyOf(this.displayedTasks);
    }

    /**
     * Marks the displayed task at the given index as complete.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The marked task.
     */
    public Task markTask(int index) {
        Task task = this.displayedTasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the displayed task at the given index as incomplete.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The unmarked task.
     */
    public Task unmarkTask(int index) {
        Task task = this.displayedTasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the displayed task at the given index.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The removed task.
     */
    public Task deleteTask(int index) {
        Task task = this.displayedTasks.remove(index);
        this.tasks.remove(task);
        return task;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return this.tasks.size();
    }

    /** Returns the number of tasks in the current displayed view. */
    public int getDisplayedTaskCount() {
        return this.displayedTasks.size();
    }

    /** Returns a read-only snapshot of the current tasks. */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }
}
