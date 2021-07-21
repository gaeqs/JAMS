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

package net.jamsimulator.jams.event.file;

import net.jamsimulator.jams.event.Event;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

/**
 * This event informs about any modification in a file.
 * <p>
 * This event is used by {@link FolderEventBroadcast}s.
 */
public class FileEvent extends Event {

    private final WatchKey key;
    private final Path path;
    private final WatchEvent<Path> watchEvent;

    public FileEvent(WatchKey key, Path path, WatchEvent<Path> watchEvent) {
        this.key = key;
        this.path = path;
        this.watchEvent = watchEvent;
    }

    /**
     * The {@link WatchKey} representing the parent folder in the {@link FolderEventBroadcast}.
     *
     * @return the {@link WatchKey}
     */
    public WatchKey getKey() {
        return key;
    }

    /**
     * Represents the {@link Path} to the file as an absolute path.
     *
     * @return the {@link Path}
     */
    public Path getPath() {
        return path;
    }

    /**
     * Represents the information of this {@link Event}.
     *
     * @return the informaiton.
     */
    public WatchEvent<Path> getWatchEvent() {
        return watchEvent;
    }
}
