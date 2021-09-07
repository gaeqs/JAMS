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

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.event.file.FileEvent;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.gui.editor.code.indexing.event.IndexFinishEditEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionAddFileEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionChangeIndexEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionRemoveFileEvent;
import net.jamsimulator.jams.gui.editor.holder.FileEditorHolderHolder;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.task.LanguageTask;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.util.*;

public abstract class ProjectGlobalIndex extends SimpleEventBroadcast implements FileCollection {

    private final Project project;
    private final Map<File, EditorIndex> indices;
    private final List<File> order;

    public ProjectGlobalIndex(Project project) {
        this.project = project;
        this.indices = new HashMap<>();
        this.order = new LinkedList<>();

        project.getProjectTab().ifPresent(tab -> tab.registerListeners(this, true));
    }

    public Project getProject() {
        return project;
    }

    @Override
    public synchronized List<File> getFiles() {
        return List.copyOf(order);
    }

    @Override
    public synchronized boolean containsFile(File file) {
        return indices.containsKey(file);
    }

    public synchronized Optional<EditorIndex> getIndex(File file) {
        return Optional.ofNullable(indices.get(file));
    }

    public synchronized <R extends EditorReferencedElement>
    Optional<R> searchReferencedElement(EditorElementReference<R> reference) {
        for (EditorIndex index : indices.values()) {
            index.lock(false);
            try {
                var optional = index.getReferencedElement(reference, true);
                if (optional.isPresent()) return optional;
            } finally {
                index.unlock(false);
            }
        }
        return Optional.empty();
    }


    public synchronized <R extends EditorReferencedElement>
    Set<EditorReferencingElement> searchReferencingElements(EditorElementReference<R> reference) {
        var set = new HashSet<EditorReferencingElement>();
        indices.values().forEach(index ->
                index.withLock(false, i -> set.addAll(i.getReferecingElements(reference))));
        return set;
    }

    @Override
    public boolean addFile(File file) {
        return addFile(file, true);
    }

    private synchronized boolean addFile(File file, boolean initialize) {
        Validate.notNull(file, "File cannot be null!");
        if (indices.containsKey(file)) return false;

        var before =
                callEvent(new FileCollectionAddFileEvent.Before(this, file));
        if (before.isCancelled()) return false;
        file = before.getFile();

        var index = getIndexFromEditor(file);
        if (index == null) index = generateIndexForFile(file);
        indices.put(file, index);
        order.add(file);

        index.withLock(true, i -> i.setGlobalIndex(this));

        if (initialize && !index.isInitialized()) {
            indexFiles(Map.of(file, index));
        } else {
            index.registerListeners(this, true);
        }

        callEvent(new FileCollectionAddFileEvent.After(this, file));
        return true;
    }

    @Override
    public boolean removeFile(File file) {
        return removeFile(file, false);
    }

    private synchronized boolean removeFile(File file, boolean force) {
        if (!indices.containsKey(file)) return false;

        var before =
                callEvent(new FileCollectionRemoveFileEvent.Before(this, file));
        if (!force && before.isCancelled()) return false;

        var index = indices.remove(file);
        order.remove(file);

        index.withLock(true, i -> {
            if (i.isInitialized()) {
                i.unregisterListeners(this);
                i.setGlobalIndex(null);
            }
        });

        return true;
    }

    @Override
    public synchronized boolean moveFile(File file, int index) {
        if (!indices.containsKey(file)) return false;
        if (index < 0 || index >= order.size()) return false;

        int old = order.indexOf(file);

        var before =
                callEvent(new FileCollectionChangeIndexEvent.Before(this, file, old, index));
        if (before.isCancelled()) return false;
        index = before.getNewIndex();
        if (index >= order.size()) return false;

        order.remove(file);
        order.add(index, file);

        callEvent(new FileCollectionChangeIndexEvent.After(this, file, old, index));
        return true;
    }

    public synchronized void saveFiles(File file) throws IOException {
        Validate.notNull(file, "File cannot be null!");
        var array = new JSONArray();
        var path = project.getFolder().toPath();
        order.stream().map(it -> path.relativize(it.toPath())).forEach(array::put);
        Files.writeString(file.toPath(), array.toString(1), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    public synchronized void loadFiles(File file) throws IOException {
        Validate.notNull(file, "File cannot be null!");
        if (!file.isFile()) return;
        new JSONArray(Files.readString(file.toPath())).toList().stream()
                .map(it -> new File(project.getFolder(), it.toString()))
                .filter(File::isFile)
                .forEach(f -> addFile(f, false));

        var newIndices = new HashMap<File, EditorIndex>();

        indices.forEach((f, index) -> {
            if (!index.isInitialized()) {
                newIndices.put(f, index);
            } else {
                index.registerListeners(this, true);
                refreshAll(null);
            }
        });

        indexFiles(newIndices);
    }

    public synchronized void refreshAll(EditorIndex ignored) {
        for (EditorIndex index : indices.values()) {
            if (index != ignored && index.isInitialized()) {
                index.requestRefresh();
            }
        }
    }

    protected void indexFiles(Map<File, EditorIndex> indices) {
        if (indices.isEmpty()) {
            refreshAll(null);
            return;
        }
        project.getTaskExecutor().execute(new LanguageTask<Void>(Messages.EDITOR_INDEXING) {
            @Override
            protected Void call() {
                indices.forEach((file, index) -> {
                    index.withLock(true, i -> {
                        try {
                            i.indexAll(Files.readString(file.toPath()));
                        } catch (IOException e) {
                            System.err.println("Errror while indexing file " + file + "!");
                            e.printStackTrace();
                        }
                    });
                    index.registerListeners(this, true);
                });
                refreshAll(null);
                return null;
            }
        });
    }

    protected abstract EditorIndex generateIndexForFile(File file);

    protected EditorIndex getIndexFromEditor(File file) {
        var projectTab = project.getProjectTab().orElse(null);
        if (projectTab == null) return null;
        var workingPane = projectTab.getProjectTabPane().getWorkingPane();
        if (!(workingPane instanceof FileEditorHolderHolder holderHolder)) return null;
        var holder = holderHolder.getFileEditorHolder();
        var tab = holder.getFileDisplayTab(file, false).orElse(null);
        if (tab == null) return null;
        var editor = tab.getDisplay();
        if (editor instanceof CodeFileEditor code) return code.getIndex();
        return null;
    }

    @Listener
    private void onFileRemove(FileEvent event) {
        if (event.getWatchEvent().kind() != StandardWatchEventKinds.ENTRY_DELETE) return;
        var file = event.getPath().toFile();
        if (!removeFile(file, true)) {
            System.err.println("Couldn't delete file " + file + " from global index.");
        }
    }

    @Listener
    private void finishEditEvent(IndexFinishEditEvent event) {
        refreshAll(event.getIndex());
    }

}
