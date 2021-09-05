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

public class FileCollectionAddFileEvent extends FileCollectionEvent {

    protected File file;

    private FileCollectionAddFileEvent(FileCollection globalIndex, File file) {
        super(globalIndex);
        Validate.notNull(file, "File cannot be null!");
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public static class Before extends FileCollectionAddFileEvent implements Cancellable {

        private boolean cancelled;

        public Before(FileCollection globalIndex, File file) {
            super(globalIndex, file);
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

        public void setFile(File file) {
            Validate.notNull(file, "File cannot be null!");
            this.file = file;
        }

    }

    public static class After extends FileCollectionAddFileEvent {
        public After(FileCollection globalIndex, File file) {
            super(globalIndex, file);
        }
    }

}
