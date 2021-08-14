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

package net.jamsimulator.jams.project.mips;

import javafx.application.Platform;
import javafx.scene.Node;
import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorHolder;
import net.jamsimulator.jams.gui.editor.FileEditorTab;
import net.jamsimulator.jams.gui.mips.editor.MIPSFileEditor;
import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.project.FilesToAssemble;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileIndexChangedFromAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;
import net.jamsimulator.jams.utils.FileUtils;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MIPSFilesToAssemble extends SimpleEventBroadcast implements FilesToAssemble {

    public static final String FILE_NAME = "files_to_assemble.json";

    private final MIPSProject project;
    private final List<File> files;
    private final Map<File, MIPSFileElements> fileElements;
    private final Bag<String> globalLabels;

    public MIPSFilesToAssemble(MIPSProject project) {
        this.project = project;
        files = new ArrayList<>();
        fileElements = new HashMap<>();
        globalLabels = new Bag<>();
    }

    public Optional<MIPSFileElements> getFileElements(File file) {
        return Optional.ofNullable(fileElements.get(file));
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public boolean supportsGlobalLabels() {
        return true;
    }

    @Override
    public Bag<String> getGlobalLabels() {
        return globalLabels;
    }

    @Override
    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public boolean containsFile(File file) {
        return files.contains(file);
    }

    @Override
    public void addFile(File file, boolean refreshGlobalLabels) {
        Validate.notNull(file, "File cannot be null!");
        if (files.contains(file)) return;

        FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
        if (before.isCancelled()) return;

        MIPSFileElements elements = new MIPSFileElements(project);
        elements.setFilesToAssemble(this);

        try {
            String text = FileUtils.readAll(file);
            elements.refreshAll(text);
            files.add(file);
            fileElements.put(file, elements);

            if (refreshGlobalLabels) {
                refreshGlobalLabels();
            }

            callEvent(new FileAddToAssembleEvent.After(file));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addFile(File file, MIPSFileElements elements, boolean refreshGlobalLabels) {
        Validate.notNull(file, "File cannot be null!");
        Validate.notNull(elements, "Elements cannot be null!");

        FileAddToAssembleEvent.Before before = callEvent(new FileAddToAssembleEvent.Before(file));
        if (before.isCancelled()) return;

        if (files.contains(file)) return;
        files.add(file);
        fileElements.put(file, elements);
        elements.setFilesToAssemble(this);

        if (refreshGlobalLabels) {
            refreshGlobalLabels();
        }

        callEvent(new FileAddToAssembleEvent.After(file));
    }

    @Override
    public void addFile(File file, FileEditorHolder holder, boolean refreshGlobalLabels) {
        Validate.notNull(file, "File cannot be null!");
        Validate.notNull(holder, "List cannot be null!");

        Optional<FileEditorTab> tab = holder.getFileDisplayTab(file, true);
        if (tab.isEmpty() || !(tab.get().getDisplay() instanceof MIPSFileEditor)) {
            addFile(file, refreshGlobalLabels);
            return;
        }
        addFile(file, ((MIPSFileEditor) tab.get().getDisplay()).getElements(), refreshGlobalLabels);
    }

    @Override
    public void removeFile(File file) {
        Validate.notNull(file, "File cannot be null!");

        FileRemoveFromAssembleEvent.Before before = callEvent(new FileRemoveFromAssembleEvent.Before(file));
        if (before.isCancelled()) return;

        if (!files.contains(file)) return;
        files.remove(file);
        var elements = fileElements.remove(file);
        elements.setFilesToAssemble(null);
        refreshDeletedDisplay(file, elements);

        refreshGlobalLabels();

        callEvent(new FileRemoveFromAssembleEvent.After(file));
    }

    @Override
    public boolean moveFileToIndex(File file, int index) {
        if (!files.contains(file) || index < 0 || index >= files.size()) return false;
        int old = files.indexOf(file);
        var before =
                callEvent(new FileIndexChangedFromAssembleEvent.Before(file, old, index));
        if (before.isCancelled()) return false;
        index = before.getNewIndex();

        if (index < 0 || index >= files.size()) return false;
        files.remove(file);
        files.add(index, file);
        callEvent(new FileIndexChangedFromAssembleEvent.After(file, old, index));
        return true;
    }

    @Override
    public void refreshGlobalLabels() {
        Set<String> toUpdate = new HashSet<>(globalLabels);

        globalLabels.clear();
        for (MIPSFileElements elements : fileElements.values()) {
            globalLabels.addAll(elements.getExistingGlobalLabels());
        }

        toUpdate.addAll(globalLabels);

        ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);

        if (tab == null) return;
        Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
        if (!(node instanceof FileEditorHolder holder)) return;

        fileElements.forEach((file, elements) -> {
            elements.seachForLabelsUpdates(toUpdate);
            Optional<FileEditorTab> fTab = holder.getFileDisplayTab(file, true);
            if (fTab.isPresent()) {
                FileEditor display = fTab.get().getDisplay();
                if (display instanceof MIPSFileEditor) {
                    elements.update(((MIPSFileEditor) display));
                }
            }
        });
    }

    public void load(File folder) throws IOException {
        File file = new File(folder, FILE_NAME);
        if (!file.isFile()) return;

        String value = FileUtils.readAll(file);

        JSONArray array = new JSONArray(value);
        for (Object element : array) {
            file = new File(project.getFolder(), element.toString());
            if (!file.isFile()) continue;
            addFile(file, false);
        }

        Platform.runLater(this::refreshGlobalLabels);
    }

    public void save(File folder) throws IOException {
        Validate.notNull(folder, "Folder cannot be null!");
        File file = new File(folder, FILE_NAME);
        JSONArray array = new JSONArray();
        Path projectPath = project.getFolder().toPath();
        files.stream().map(target -> projectPath.relativize(target.toPath())).forEach(array::put);

        Writer writer = new FileWriter(file);
        writer.write(array.toString(1));
        writer.close();
    }

    @Override
    public void checkFiles() {
        var toRemove = files.stream().filter(target -> !target.isFile()).collect(Collectors.toList());
        for (File file : toRemove) {
            removeFile(file);
        }
    }

    private void refreshDeletedDisplay(File file, MIPSFileElements elements) {
        ProjectTab tab = JamsApplication.getProjectsTabPane().getProjectTab(project).orElse(null);
        if (tab == null) return;
        Node node = tab.getProjectTabPane().getWorkingPane().getCenter();
        if (!(node instanceof FileEditorHolder holder)) return;

        Optional<FileEditorTab> fTab = holder.getFileDisplayTab(file, true);

        elements.seachForLabelsUpdates(globalLabels);
        if (fTab.isPresent()) {
            FileEditor display = fTab.get().getDisplay();
            if (display instanceof MIPSFileEditor) {
                elements.update(((MIPSFileEditor) display));
            }
        }
    }
}
