/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package task;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.general.JAMSShutdownEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Instances of this class allows developers to execute asynchronous tasks easily.
 * <p>
 * To execute a task, use the method {@link #execute(String, String, Runnable)} or {@link #execute(String, String, Callable)}.
 * {@link Callable}s can be {@link ProgressableTask}. This executor will handle them automatically.
 * <p>
 * The tasks will be wrapped in a {@link JamsTask} record. You can access to all tasks being executed using
 * {@link #getTasks()}. This method creates a copy of the tasks' list.
 * <p>
 * If you only want to get the first task you can use {@link #getFirstTask()}. This way you won't create any new list.
 * <p>
 * The executor inside this class will be shut down when the JAMS application is closed
 * or when the method {@link #shutdown()} or {@link #shutdownNow()} is invoked.
 */
public class TaskExecutor {

    private final ExecutorService executor;
    private final LinkedList<JamsTask> tasks;

    /**
     * Creates the task executor.
     */
    public TaskExecutor() {
        this.executor = Executors.newCachedThreadPool();
        tasks = new LinkedList<>();
        Jams.getGeneralEventBroadcast().registerListeners(this, true);
    }

    /**
     * Returns a new list with all tasks being executed in this executor.
     *
     * @return the new list.
     */
    public synchronized List<JamsTask> getTasks() {
        tasks.removeIf(it -> it.future().isDone());
        return new LinkedList<>(tasks);
    }

    /**
     * Returns the first {@link JamsTask task} of the tasks' list if present.
     *
     * @return the first {@link JamsTask task} if present.
     */
    public synchronized Optional<JamsTask> getFirstTask() {
        tasks.removeIf(it -> it.future().isDone());
        return Optional.ofNullable(tasks.isEmpty() ? null : tasks.getFirst());
    }

    /**
     * Executes the given runnable in this executor.
     * You must provide a name to the task.
     *
     * @param name         the name of the task.
     * @param languageNode the language node of the task. It may be null.
     * @param runnable     the code to execute.
     * @return the {@link JamsTask} representing the task being executed.
     * @see ExecutorService#submit(Runnable)
     */
    public synchronized JamsTask execute(String name, String languageNode, Runnable runnable) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(runnable, "Runnable cannot be null!");

        tasks.removeIf(it -> it.future().isDone());
        var task = executor.submit(runnable);
        var jamsTask = new JamsTask(name, languageNode, task, null);
        tasks.add(jamsTask);
        return jamsTask;
    }

    /**
     * Executes the given runnable in this executor.
     * You must provide a name to the task.
     *
     * @param name         the name of the task.
     * @param languageNode the language node of the task. It may be null.
     * @param runnable     the code to execute.
     * @return the {@link JamsTask} representing the task being executed.
     * @see ExecutorService#submit(Runnable)
     */
    public synchronized JamsTask execute(String name, String languageNode, Callable<?> runnable) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(runnable, "Runnable cannot be null!");

        tasks.removeIf(it -> it.future().isDone());
        var task = executor.submit(runnable);
        var jamsTask = new JamsTask(name, languageNode, task,
                runnable instanceof ProgressableTask p ? p.progressProperty() : null);
        tasks.add(jamsTask);
        return jamsTask;
    }

    /**
     * Returns whether this executor is shut down.
     *
     * @return whether this executor is shut down.
     * @see ExecutorService#isShutdown()
     */
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    /**
     * Returns whether this executor is shut down and all its tasks have finished.
     *
     * @return whether this executor is shut down and all its tasks have finished.
     * @see ExecutorService#isTerminated()
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    /**
     * Shutdowns this executor, avoiding new tasks to be accepted.
     * Running tasks will still be executed.
     *
     * @see ExecutorService#shutdown()
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Shutdowns this executor, killing all running tasks.
     *
     * @see ExecutorService#shutdownNow()
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    @Listener
    private void onShutdown(JAMSShutdownEvent.Before event) {
        executor.shutdownNow();
    }

}
