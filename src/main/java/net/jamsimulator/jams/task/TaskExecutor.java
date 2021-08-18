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

package net.jamsimulator.jams.task;

import javafx.concurrent.Task;
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
import java.util.concurrent.FutureTask;

/**
 * Instances of this class allows developers to execute asynchronous tasks easily.
 * <p>
 * To execute a net.jamsimulator.jams.task, use the method {@link #execute(String, Runnable)},
 * {@link #execute(String, Callable)} or {@link #execute(String, Task)}. If you use the last one
 * you can configure your task to implement a progression bar.
 * <p>
 * The runnables will be wrapped in a {@link Task} instance. You can access to all tasks being executed using
 * {@link #getTasks()}. This method creates a copy of the tasks' list.
 * <p>
 * If you only want to get the first {@link Task} you can use {@link #getFirstTask()}.
 * This way you won't create any new list.
 * <p>
 * The executor inside this class will be shut down when the JAMS application is closed
 * or when the method {@link #shutdown()} or {@link #shutdownNow()} is invoked.
 * <p>
 * Avoid creating tasks that invoke blocking methods: the thread used by the task won't be released while the thread
 * is waiting.
 */
public class TaskExecutor {

    private final ExecutorService executor;
    private final LinkedList<Task<?>> tasks;

    /**
     * Creates the net.jamsimulator.jams.task executor.
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
    public synchronized List<Task<?>> getTasks() {
        tasks.removeIf(FutureTask::isDone);
        return new LinkedList<>(tasks);
    }

    /**
     * Returns the first {@link Task} of the tasks' list if present.
     *
     * @return the first {@link Task} if present.
     */
    public synchronized Optional<Task<?>> getFirstTask() {
        tasks.removeIf(FutureTask::isDone);
        return Optional.ofNullable(tasks.isEmpty() ? null : tasks.getFirst());
    }

    /**
     * Executes the given {@link Runnable} in this executor.
     * You must provide a name to the task.
     *
     * @param name     the name of the task.
     * @param runnable the code to execute.
     * @return the {@link Task} being executed.
     * @see ExecutorService#submit(Runnable)
     */
    public synchronized Task<?> execute(String name, String title, Runnable runnable) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(runnable, "Runnable cannot be null!");

        tasks.removeIf(FutureTask::isDone);
        var task = new Task<>() {

            {
                updateTitle(title);
            }

            @Override
            protected Object call() throws Exception {
                runnable.run();
                return null;
            }
        };

        executor.submit(task);
        tasks.add(task);
        return task;
    }

    /**
     * Executes the given {@link Callable} in this executor.
     * You must provide a name to the task
     *
     * @param name     the name of the task.
     * @param callable the code to execute.
     * @return the {@link Task} being executed.
     * @see ExecutorService#submit(Runnable)
     */
    public synchronized <T> Task<T> execute(String name, String title, Callable<T> callable) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(callable, "Callable cannot be null!");

        tasks.removeIf(FutureTask::isDone);
        var task = new Task<T>() {

            {
                updateTitle(title);
            }

            @Override
            protected T call() throws Exception {
                return callable.call();
            }
        };

        executor.submit(task);
        tasks.add(task);
        return task;
    }

    /**
     * Executes the given runnable in this executor.
     * You must provide a name to the task
     *
     * @param name the name of the task.
     * @param task the task to execute.
     * @see ExecutorService#submit(Runnable)
     */
    public synchronized <T> void execute(String name, Task<T> task) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(task, "Task cannot be null!");
        tasks.removeIf(FutureTask::isDone);
        executor.submit(task);
        tasks.add(task);
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
