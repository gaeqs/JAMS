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

import javafx.application.Platform;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * This class implements a {@link EventBroadcast} that listens to changes in its registered directories.
 * <p>
 * When a file is created, removed or changed inside any of the
 * recorded directories, this broadcasts emits a {@link FileEvent}.
 */
public class FolderEventBroadcast extends SimpleEventBroadcast {

    private final WatchService service;
    private boolean running;
    private final EventBroadcast parentBroadcast;

    /**
     * Creates the folder event broadcast.
     */
    public FolderEventBroadcast() {
        this(null);
    }

    /**
     * Creates the folder event broadcast using a parent event broadcast.
     * Events called through this broadcast will be assigned to the given parent broadcast.
     *
     * @param parentBroadcast the parent broadcast.
     */
    public FolderEventBroadcast(EventBroadcast parentBroadcast) {
        try {
            service = FileSystems.getDefault().newWatchService();
            running = true;
            this.parentBroadcast = parentBroadcast == null ? this : parentBroadcast;
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't init folder listener", ex);
        }
    }

    /**
     * Registers the given directory.
     *
     * @param directory the directory.
     * @return the {@link WatchKey} representing the registration of the given path.
     * @throws IOException any IOException thrown during registration.
     * @see Path#register(WatchService, WatchEvent.Kind[])
     */
    public WatchKey registerPath(Path directory) throws IOException {
        if (!running) throw new IllegalStateException("FolderListener not running!");
        return directory.toAbsolutePath().register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    /**
     * Registers the given directory and all its children directories, recursively.
     *
     * @param directory the directory.
     * @throws IOException any IOException thrown during registration.
     * @see Path#register(WatchService, WatchEvent.Kind[])
     */
    public void registerPathRecursively(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerPath(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Kills the event broadcast, making it unable to send more {@link FileEvent}s.
     *
     * @throws IOException any IOException thrown by the close process.
     * @see WatchService#close()
     */
    public void kill() throws IOException {
        running = false;
        service.close();
    }

    /**
     * Executes the listener processor.
     * This method blocks the caller thread.
     */
    public void folderListenerProcessor() {
        WatchKey key;
        while (running) {
            try {
                key = service.take();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                running = false;
                return;
            } catch (ClosedWatchServiceException ex) {
                // Just finish the method.
                running = false;
                return;
            }

            for (var event : key.pollEvents()) {
                var kind = event.kind();
                if (kind == OVERFLOW) continue;
                var casted = (WatchEvent<Path>) event;
                var path = ((Path) key.watchable()).resolve(casted.context());
                var callingEvent = new FileEvent(key, path, casted);
                Platform.runLater(() -> callEvent(callingEvent, parentBroadcast));
            }
            key.reset();
        }
    }

}
