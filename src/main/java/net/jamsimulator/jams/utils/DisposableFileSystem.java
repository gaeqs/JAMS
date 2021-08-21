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

package net.jamsimulator.jams.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DisposableFileSystem {

    private static final Map<FileSystem, Integer> MAP = new ConcurrentHashMap<>();

    private FileSystem fileSystem;
    private final boolean closeable;

    public DisposableFileSystem(URI uri) throws IOException {
        if (uri.getScheme().equals("file")) {
            fileSystem = FileSystems.getDefault();
            closeable = false;
        } else {
            try {
                fileSystem = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException ex) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            }
            closeable = true;
            MAP.merge(fileSystem, 1, Integer::sum);
        }
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public boolean isClosed() {
        return fileSystem == null || !fileSystem.isOpen();
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void close() throws IOException {
        if (!closeable || isClosed()) return;

        MAP.computeIfPresent(fileSystem, (f, count) -> count - 1);
        if (MAP.getOrDefault(fileSystem, 0) == 0) {
            MAP.remove(fileSystem);
            fileSystem.close();
        }

        fileSystem = null;
    }

}
