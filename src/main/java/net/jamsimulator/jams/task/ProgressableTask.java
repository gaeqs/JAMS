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

import javafx.beans.property.FloatProperty;

import java.util.concurrent.Callable;

/**
 * Represents a net.jamsimulator.jams.task whose progress can be tracked.
 * <p>
 * This interface extends {@link Callable}: you can use it on a {@link TaskExecutor}.
 *
 * @param <T> the return type of the net.jamsimulator.jams.task.
 */
public interface ProgressableTask<T> extends Callable<T> {

    /**
     * Returns the property representing the progress.
     * <p>
     * WARNING! Update this progress' value in the JavaFX's thread, as it may be bound to a Node's property
     * that requires thread-safety.
     *
     * @return the progress property.
     */
    FloatProperty progressProperty();

}
