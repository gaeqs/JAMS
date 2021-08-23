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
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a {@link Task} whose methods {@link #updateTitle(String)} and {@link #updateMessage(String)}
 * requires a language node instead of a raw string.
 * <p>
 * The title and the message will be updated automatically when the selected or default language is changed.
 * <p>
 * Update methods are made public in this class. This is made to update titles and messages easily using
 * the static methods.
 * <p>
 * You can use the static method to create {@link LanguageTask}s using runnables and callables. You can also
 * use functions that requires a {@link LanguageTask} as a parameter to create code that require updating
 * the progress, title, message or value of the task.
 *
 * @param <E> the type of the task.
 * @see Task
 */
public abstract class LanguageTask<E> extends Task<E> {

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Runnable}.
     *
     * @param runnable the runnable.
     * @return the task.
     */
    public static LanguageTask<?> of(Runnable runnable) {
        return of(null, null, runnable);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Runnable}.
     * <p>
     * You can provide a title to the task.
     *
     * @param runnable          the runnable.
     * @param titleLanguageNode the title as a language node.
     * @return the task.
     */
    public static LanguageTask<?> of(String titleLanguageNode, Runnable runnable) {
        return of(titleLanguageNode, null, runnable);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Runnable}.
     * <p>
     * You can provide a title and a message to the task.
     *
     * @param runnable            the runnable.
     * @param titleLanguageNode   the title as a language node.
     * @param messageLanguageNode the message as a language node.
     * @return the task.
     */
    public static LanguageTask<?> of(String titleLanguageNode, String messageLanguageNode, Runnable runnable) {
        return new LanguageTask<>(titleLanguageNode, messageLanguageNode) {
            @Override
            protected Object call() {
                if (runnable != null) {
                    runnable.run();
                }
                return null;
            }
        };
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Callable}.
     *
     * @param callable the callable.
     * @param <E>      the type of the callable.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(Callable<E> callable) {
        return of(null, null, callable);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Callable}.
     * <p>
     * You can provide a title to the task.
     *
     * @param callable          the callable.
     * @param titleLanguageNode the title as a language node.
     * @param <E>               the type of the callable.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, Callable<E> callable) {
        return of(titleLanguageNode, null, callable);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Callable}.
     * <p>
     * You can provide a title and a message to the task.
     *
     * @param callable            the callable.
     * @param titleLanguageNode   the title as a language node.
     * @param messageLanguageNode the message as a language node.
     * @param <E>                 the type of the callable.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, String messageLanguageNode, Callable<E> callable) {
        return new LanguageTask<>(titleLanguageNode, messageLanguageNode) {
            @Override
            protected E call() throws Exception {
                return callable.call();
            }
        };
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Consumer}.
     * Use the {@link LanguageTask} provided by the given consumer to edit the {@link LanguageTask}.
     *
     * @param consumer the consumer.
     * @param <E>      the type of the consumer.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(Consumer<LanguageTask<E>> consumer) {
        return of(null, null, consumer);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Consumer}.
     * Use the {@link LanguageTask} provided by the given consumer to edit the {@link LanguageTask}.
     * <p>
     * You can provide a title to the task.
     *
     * @param consumer          the consumer.
     * @param titleLanguageNode the title as a language node.
     * @param <E>               the type of the consumer.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, Consumer<LanguageTask<E>> consumer) {
        return of(titleLanguageNode, null, consumer);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Consumer}.
     * Use the {@link LanguageTask} provided by the given consumer to edit the {@link LanguageTask}.
     * <p>
     * You can provide a title and a message to the task.
     *
     * @param consumer            the consumer.
     * @param titleLanguageNode   the title as a language node.
     * @param messageLanguageNode the message as a language node.
     * @param <E>                 the type of the consumer.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, String messageLanguageNode,
                                         Consumer<LanguageTask<E>> consumer) {
        return new LanguageTask<>(titleLanguageNode, messageLanguageNode) {
            @Override
            protected E call() {
                if (consumer != null) {
                    consumer.accept(this);
                }
                return null;
            }
        };
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Function}.
     * Use the {@link LanguageTask} provided by the given function to edit the {@link LanguageTask}.
     *
     * @param function the function.
     * @param <E>      the type of the function.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(Function<LanguageTask<E>, E> function) {
        return of(null, null, function);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Function}.
     * Use the {@link LanguageTask} provided by the given function to edit the {@link LanguageTask}.
     * <p>
     * You can provide a title to the task.
     *
     * @param function          the function.
     * @param titleLanguageNode the title as a language node.
     * @param <E>               the type of the function.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, Function<LanguageTask<E>, E> function) {
        return of(titleLanguageNode, null, function);
    }

    /**
     * Creates a new {@link LanguageTask} that runs the given {@link Function}.
     * Use the {@link LanguageTask} provided by the given function to edit the {@link LanguageTask}.
     * <p>
     * You can provide a title and a message to the task.
     *
     * @param function            the function.
     * @param titleLanguageNode   the title as a language node.
     * @param messageLanguageNode the message as a language node.
     * @param <E>                 the type of the function.
     * @return the task.
     */
    public static <E> LanguageTask<E> of(String titleLanguageNode, String messageLanguageNode,
                                         Function<LanguageTask<E>, E> function) {
        return new LanguageTask<>(titleLanguageNode, messageLanguageNode) {
            @Override
            protected E call() {
                return function.apply(this);
            }
        };
    }

    private String titleLanguageNode;
    private String messageLanguageNode;

    /**
     * Creates the language task.
     */
    public LanguageTask() {
        this(null, null);
    }

    /**
     * Creates the language task.
     *
     * @param titleLanguageNode the default title as a language node.
     */
    public LanguageTask(String titleLanguageNode) {
        this(titleLanguageNode, null);
    }

    /**
     * Creates the language task.
     *
     * @param titleLanguageNode   the default title as a language node.
     * @param messageLanguageNode the default message as a language node.
     */
    public LanguageTask(String titleLanguageNode, String messageLanguageNode) {
        updateTitle(titleLanguageNode);
        updateMessage(messageLanguageNode);
        Jams.getLanguageManager().registerListeners(this, true);
    }

    /**
     * Updates the language node of the title.
     * The title is updates the same way a normal title would be updated in a {@link Task}.
     * See {@link Task#updateTitle(String)} for more information.
     *
     * @param title the title.
     * @see Task#updateTitle(String)
     */
    @Override
    public void updateTitle(String title) {
        titleLanguageNode = title;
        super.updateTitle(title == null ? null : Jams.getLanguageManager().getDefault().getOrDefault(title));
    }

    /**
     * Updates the language node of the message.
     * The message is updates the same way a normal message would be updated in a {@link Task}.
     * See {@link Task#updateMessage(String)}} for more information.
     *
     * @param message the message.
     * @see Task#updateMessage(String)
     */
    @Override
    public void updateMessage(String message) {
        messageLanguageNode = message;
        super.updateMessage(message);
        super.updateTitle(message == null ? null : Jams.getLanguageManager().getDefault().getOrDefault(message));
    }

    @Override
    public void updateProgress(long workDone, long max) {
        super.updateProgress(workDone, max);
    }

    @Override
    public void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }

    @Override
    public void updateValue(E value) {
        super.updateValue(value);
    }

    @Listener
    private void onLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        updateTitle(titleLanguageNode);
        updateMessage(messageLanguageNode);
    }

    @Listener
    private void onLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        updateTitle(titleLanguageNode);
        updateMessage(messageLanguageNode);
    }


}
