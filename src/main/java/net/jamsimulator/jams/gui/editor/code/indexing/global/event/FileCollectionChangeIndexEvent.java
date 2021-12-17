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

package net.jamsimulator.jams.gui.editor.code.indexing.global.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.gui.editor.code.indexing.global.FileCollection;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

/**
 * This event is fired when a file inside a {@link FileCollection} changes its index.
 */
public class FileCollectionChangeIndexEvent extends FileCollectionEvent {

    protected final File file;
    protected final int oldIndex;
    protected int newIndex;

    public FileCollectionChangeIndexEvent(FileCollection globalIndex, File file, int oldIndex, int newIndex) {
        super(globalIndex);
        Validate.notNull(file, "File cannot be null!");
        Validate.isTrue(oldIndex >= 0, "Index must be positive!");
        Validate.isTrue(newIndex >= 0, "Index must be positive!");
        this.file = file;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    public File getFile() {
        return file;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public int getNewIndex() {
        return newIndex;
    }

    public static class Before extends FileCollectionChangeIndexEvent implements Cancellable {

        private boolean cancelled;

        public Before(FileCollection globalIndex, File file, int oldIndex, int newIndex) {
            super(globalIndex, file, oldIndex, newIndex);
            this.cancelled = false;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public void setNewIndex(int index) {
            Validate.isTrue(index >= 0, "Index must be positive!");
            this.newIndex = index;
        }

    }

    public static class After extends FileCollectionChangeIndexEvent {
        public After(FileCollection globalIndex, File file, int oldIndex, int newIndex) {
            super(globalIndex, file, oldIndex, newIndex);
        }
    }

}
