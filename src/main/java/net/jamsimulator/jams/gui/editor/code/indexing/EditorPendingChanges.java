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

package net.jamsimulator.jams.gui.editor.code.indexing;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Helper class that stores the changes pending to be indexed.
 * <p>
 * This class is thread-safe.
 */
public class EditorPendingChanges {

    private final ConcurrentLinkedQueue<EditorLineChange> changes = new ConcurrentLinkedQueue<>();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmptyCondition = lock.newCondition();

    private boolean reformat;

    public EditorPendingChanges add(EditorLineChange change) {
        lock.lock();
        changes.add(change);
        notEmptyCondition.signalAll();
        lock.unlock();
        return this;
    }

    public EditorPendingChanges add(PlainTextChange change, CodeArea area) {
        lock.lock();
        EditorLineChange.of(change, area, changes);
        if (!changes.isEmpty()) {
            notEmptyCondition.signalAll();
        }
        lock.unlock();
        return this;
    }

    public EditorPendingChanges addAll(Collection<? extends EditorLineChange> changes) {
        lock.lock();
        this.changes.addAll(changes);
        if (!changes.isEmpty()) {
            notEmptyCondition.signalAll();
        }
        lock.unlock();
        return this;
    }

    public EditorPendingChanges addAll(Collection<? extends PlainTextChange> changes, CodeArea area) {
        lock.lock();
        changes.forEach(change -> EditorLineChange.of(change, area, this.changes));
        if (!changes.isEmpty()) {
            notEmptyCondition.signalAll();
        }
        lock.unlock();
        return this;
    }

    public boolean isEmpty() {
        try {
            lock.lock();
            return changes.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void waitForElements() throws InterruptedException {
        lock.lock();
        if (changes.isEmpty() && !reformat) {
            notEmptyCondition.await();
        }
        lock.unlock();
    }

    public void markForReformat() {
        lock.lock();
        reformat = true;
        notEmptyCondition.signalAll();
        lock.unlock();
    }

    public boolean isMarkedForReformat(boolean clear) {
        lock.lock();
        boolean b = reformat;
        if (clear) {
            reformat = false;
        }
        lock.unlock();
        return b;
    }

    public void flushAll(Consumer<? super EditorLineChange> consumer) {
        EditorLineChange previous = changes.poll();
        if (previous == null) return;
        EditorLineChange current;
        while ((current = changes.poll()) != null) {
            // Let's filter useless edit lines.

            if (previous.type() == EditorLineChange.Type.ADD
                    && current.type() == EditorLineChange.Type.EDIT
                    && current.line() == previous.line()) {
                current = new EditorLineChange(EditorLineChange.Type.ADD, current.line(), current.text());
            } else if (previous.type() != EditorLineChange.Type.EDIT
                    || current.type() != EditorLineChange.Type.EDIT
                    || current.line() != previous.line()) {
                consumer.accept(previous);
            }
            previous = current;
        }
        consumer.accept(previous);
    }
}
