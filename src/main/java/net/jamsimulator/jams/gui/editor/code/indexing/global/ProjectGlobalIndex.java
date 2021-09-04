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

import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.holder.FileEditorHolderHolder;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ProjectGlobalIndex {

    private final Project project;
    private final Map<File, EditorIndex> indices;
    private final List<File> order;

    public ProjectGlobalIndex(Project project) {
        this.project = project;
        this.indices = new HashMap<>();
        this.order = new LinkedList<>();
        loadFiles();
    }

    public Project getProject() {
        return project;
    }

    public synchronized boolean addFile(File file) {
        Validate.notNull(file, "File cannot be null!");
        if (indices.containsKey(file)) return false;
        var index = getIndexFromEditor(file);
        if (index == null) index = generateIndexForFile(file);
        indices.put(file, index);
        order.add(file);
        return true;
    }

    public synchronized boolean removeFile(File file) {
        if (indices.remove(file) == null) return false;
        order.remove(file);
        return true;
    }

    public synchronized boolean moveFile(File file, int index) {
        if (!indices.containsKey(file)) return false;
        if (index < 0 || index >= order.size()) return false;
        order.remove(file);
        order.add(index, file);
        return true;
    }

    public abstract void saveFiles();

    protected abstract void loadFiles();

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
}
