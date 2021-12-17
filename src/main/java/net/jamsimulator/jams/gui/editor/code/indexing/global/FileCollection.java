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

package net.jamsimulator.jams.gui.editor.code.indexing.global;

import net.jamsimulator.jams.event.EventBroadcast;

import java.io.File;
import java.util.List;

/**
 * Represents a basic collection of files.
 * <p>
 * Changes to this collector can be observed using the events
 * {@link net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionAddFileEvent FileCollectionAddFileEvent},
 * {@link net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionRemoveFileEvent FileCollectionRemoveFileEvent}
 * and {@link net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionChangeIndexEvent FileCollectionChangeIndexEvent}.
 */
public interface FileCollection extends EventBroadcast {

    /**
     * Returns a new {@link List} with the files inside this file collection.
     *
     * @return the new {@link List}.
     */
    List<File> getFiles();

    /**
     * Returns whether the given file is inside this collection.
     * This method returns false if the file is null.
     *
     * @param file the file.
     * @return whether the give file is inside this collection.
     */
    boolean containsFile(File file);

    /**
     * Adds the given file to this file collection.
     * The given file can't be null!
     *
     * @param file the file.
     * @return whether the operation was successful.
     */
    boolean addFile(File file);

    /**
     * Removes the given file from this file collection.
     * This method does nothing if the file is null.
     *
     * @param file the file.
     * @return whether the operation was successful.
     */
    boolean removeFile(File file);

    /**
     * Moves the given file to the given index.
     * <p>
     * This method does nothing if the file is null, or it is not present in the collection.
     *
     * @param file  the file to move.
     * @param index the new index.
     * @return whether the operation was successful.
     */
    boolean moveFile(File file, int index);
}
