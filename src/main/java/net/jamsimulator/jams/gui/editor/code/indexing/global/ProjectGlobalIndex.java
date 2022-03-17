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
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementRelativeReference;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This is the collection containing the files to assemble of a project.
 * <p>
 * These files' indices are always loaded, and they can share common information.
 */
public abstract class ProjectGlobalIndex extends SimpleEventBroadcast implements FileCollection {

    private final Project project;
    private final Map<File, EditorIndex> indices;
    private final List<File> order;

    public ProjectGlobalIndex(Project project) {
        Validate.notNull(project, "Project cannot be null!");
        this.project = project;
        this.indices = new ConcurrentHashMap<>();
        this.order = new LinkedList<>();

        project.getProjectTab().ifPresent(tab -> tab.registerListeners(this, true));
    }

    /**
     * Returns the {@link Project} of this global index.
     *
     * @return the {@link Project}.
     */
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

    /**
     * Returns the {@link EditorIndex} linked to the given index if present.
     *
     * @param file the file.
     * @return the index if present.
     */
    public synchronized Optional<EditorIndex> getIndex(File file) {
        return Optional.ofNullable(indices.get(file));
    }

    /**
     * Searches for a global {@link EditorReferencedElement} that matches
     * the given {@link EditorElementReference} inside any of the files.
     *
     * @param reference the reference.
     * @param <R>       the type of the referenced element.
     * @return the referenced element.
     */
    public <R extends EditorReferencedElement>
    Optional<R> searchReferencedElement(EditorElementReference<R> reference) {
        if (reference instanceof EditorElementRelativeReference<R>) return Optional.empty();
        for (EditorIndex index : indices.values()) {
            index.lock(false);
            try {
                var optional = index.getReferencedElement(reference, ElementScope.GLOBAL);
                if (optional.isPresent()) return optional;
            } finally {
                index.unlock(false);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for all global {@link EditorReferencedElement}s that match
     * the given {@link EditorElementReference} inside all the files.
     *
     * @param reference the reference.
     * @param <R>       the type of the referenced element.
     * @return the referenced elements.
     */
    public <R extends EditorReferencedElement>
    Set<R> searchReferencedElements(EditorElementReference<R> reference) {
        if (reference instanceof EditorElementRelativeReference<R>) return Collections.emptySet();
        return indices.values().stream()
                .flatMap(index -> index.withLockF(false,
                        i -> i.getReferencedElements(reference, ElementScope.GLOBAL).stream()))
                .collect(Collectors.toSet());
    }

    /**
     * Searches for all global {@link EditorReferencedElement}s that match the given type.
     *
     * @param type the type of the referenced element.
     * @param <R>  the type of the referenced element.
     * @return the referenced elements.
     */
    public <R extends EditorReferencedElement>
    Set<R> searchReferencedElementsOfType(Class<R> type) {
        var set = new HashSet<R>();
        indices.values().forEach(index ->
                index.withLock(false,
                        i -> set.addAll(i.getReferencedElementsOfType(type, ElementScope.GLOBAL))));
        return set;
    }

    /**
     * Searches for all {@link EditorReferencingElement} that references the given
     * {@link EditorElementReference} inside all the files.
     *
     * @param reference the reference.
     * @param <R>       the type of the referenced element.
     * @return the referencing elements.
     */
    public <R extends EditorReferencedElement>
    Set<EditorReferencingElement<?>> searchReferencingElements(EditorElementReference<R> reference) {
        var set = new HashSet<EditorReferencingElement<?>>();
        indices.values().forEach(index ->
                index.withLock(false, i -> set.addAll(i.getReferecingElements(reference))));
        return set;
    }

    /**
     * Inspects all the elements that contains the given {@link EditorElementReference}s.
     * The {@link EditorIndex indices} inside the given set will be ingnored.
     *
     * @param references     the references.
     * @param ignoredIndices the indices to ignore.
     */
    public void inspectElementsWithReferences(
            Set<EditorElementReference<?>> references, Set<EditorIndex> ignoredIndices) {
        indices.values().stream().filter(it -> !ignoredIndices.contains(it)).forEach(it ->
                it.withLock(true, index -> index.inspectElementsWithReferences(references)));
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

        if (index.isInitialized()) {
            // Inspect the added file too!
            inspectElementsWithReferences(index.withLockF(false, EditorIndex::getAllGlobalReferencedReferences),
                    Set.of());
        }

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

        if (index.isInitialized()) {
            var referenced = index.withLockF(true, i -> {
                var set = i.getAllGlobalReferencedReferences();
                i.inspectElementsWithReferences(set);
                return set;
            });

            index.withLock(true, i -> i.inspectElementsWithReferences(referenced));
            inspectElementsWithReferences(index.withLockF(false, EditorIndex::getAllGlobalReferencedReferences),
                    Set.of(index));
        }

        callEvent(new FileCollectionRemoveFileEvent.After(this, file));

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

    /**
     * Saves this collection in the given file.
     *
     * @param file the file.
     * @throws IOException when something bad happens with the file.
     */
    public synchronized void saveFiles(File file) throws IOException {
        Validate.notNull(file, "File cannot be null!");
        var array = new JSONArray();
        var path = project.getFolder().toPath();
        order.stream().map(it -> path.relativize(it.toPath())).forEach(array::put);
        Files.writeString(file.toPath(), array.toString(1), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    /**
     * Fills this collection with the contents of the given file.
     * <p>
     * The previous elements WON'T be removed.
     *
     * @param file the file.
     * @throws IOException when something bad happens with the file.
     */
    public void loadFiles(File file) throws IOException {
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

    public void refreshAll(EditorIndex ignored) {
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

        project.getTaskExecutor().execute(LanguageTask.of(Messages.EDITOR_INDEXING, () -> {
            indices.forEach((file, index) -> {
                index.withLock(true, i -> {
                    try {
                        i.indexAll(Files.readString(file.toPath()));
                    } catch (Exception e) {
                        System.err.println("Errror while indexing file " + file + "!");
                        e.printStackTrace();
                    }
                });
                index.registerListeners(this, true);
            });
            refreshAll(null);
            return null;
        }));
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
