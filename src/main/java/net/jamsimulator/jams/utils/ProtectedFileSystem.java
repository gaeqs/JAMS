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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;
import java.util.Set;

/**
 * A wrapper for {@link FileSystem} that doesn't allow users to close it.
 */
public class ProtectedFileSystem extends FileSystem {

    private final FileSystem fileSystem;

    /**
     * Creates the wrapper.
     *
     * @param fileSystem the {@link FileSystem}.
     */
    public ProtectedFileSystem(FileSystem fileSystem) {
        Validate.notNull(fileSystem, "FileSystem cannot be null!");
        this.fileSystem = fileSystem;
    }

    @Override
    public FileSystemProvider provider() {
        return fileSystem.provider();
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        return fileSystem.isOpen();
    }

    @Override
    public boolean isReadOnly() {
        return fileSystem.isReadOnly();
    }

    @Override
    public String getSeparator() {
        return fileSystem.getSeparator();
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return fileSystem.getRootDirectories();
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return fileSystem.getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return fileSystem.supportedFileAttributeViews();
    }

    @NotNull
    @Override
    public Path getPath(String first, String... more) {
        return fileSystem.getPath(first, more);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return fileSystem.getPathMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return fileSystem.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return fileSystem.newWatchService();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtectedFileSystem that = (ProtectedFileSystem) o;
        return fileSystem.equals(that.fileSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileSystem);
    }
}
