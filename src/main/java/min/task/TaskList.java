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
        assert tasks != null : "Initial task list must not be null.";

        this.tasks = new ArrayList<>(tasks);
        this.displayedTasks = new ArrayList<>(tasks);
        this.isShowingAllTasks = true;

        assert isDisplayedViewConsistent()
                : "Displayed tasks must be consistent with the full task list.";
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Added task must not be null.";

        this.tasks.add(task);
        if (this.isShowingAllTasks) {
            this.displayedTasks.add(task);
        }

        assert isDisplayedViewConsistent()
                : "Displayed tasks must be consistent with the full task list.";
    }

    /**
     * Sets the displayed view to tasks whose descriptions contain the given keyword.
     *
     * @param keyword The text to match against task descriptions.
     * @return A read-only list of matching tasks.
     */
    public List<Task> showMatchingTasks(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Find keyword must not be blank.";

        this.displayedTasks.clear();
        for (Task task : this.tasks) {
            if (task.getDescription().contains(keyword)) {
                this.displayedTasks.add(task);
            }
        }
        this.isShowingAllTasks = false;

        assert isDisplayedViewConsistent()
                : "Displayed tasks must be consistent with the full task list.";

        return List.copyOf(this.displayedTasks);
    }

    /** Restores and returns the complete task list as the displayed view. */
    public List<Task> showAllTasks() {
        this.displayedTasks.clear();
        this.displayedTasks.addAll(this.tasks);
        this.isShowingAllTasks = true;

        assert isDisplayedViewConsistent()
                : "Displayed tasks must be consistent with the full task list.";

        return List.copyOf(this.displayedTasks);
    }

    /**
     * Marks the displayed task at the given index as complete.
     *
     * @param index The zero-based index in the displayed task view.
     * @return The marked task.
     */
    public Task markTask(int index) {
        assert index >= 0 && index < this.displayedTasks.size()
                : "Task index must refer to a displayed task.";

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
        assert index >= 0 && index < this.displayedTasks.size()
                : "Task index must refer to a displayed task.";

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
        assert index >= 0 && index < this.displayedTasks.size()
                : "Task index must refer to a displayed task.";

        Task task = this.displayedTasks.remove(index);
        boolean wasRemoved = this.tasks.remove(task);

        assert wasRemoved : "Displayed task must exist in the full task list.";
        assert isDisplayedViewConsistent()
                : "Displayed tasks must be consistent with the full task list.";

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

    /**
     * Returns whether the displayed task view is consistent with the full task list.
     *
     * @return Whether every displayed task belongs to the full task list and both lists
     *         match when all tasks are being shown.
     */
    private boolean isDisplayedViewConsistent() {
        return this.tasks.containsAll(this.displayedTasks)
                && (!this.isShowingAllTasks || this.tasks.equals(this.displayedTasks));
    }
}
