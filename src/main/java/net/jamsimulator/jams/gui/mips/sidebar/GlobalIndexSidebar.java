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

package net.jamsimulator.jams.gui.mips.sidebar;

import javafx.scene.control.ListView;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.gui.editor.code.indexing.global.ProjectGlobalIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionAddFileEvent;
import net.jamsimulator.jams.gui.editor.code.indexing.global.event.FileCollectionRemoveFileEvent;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.project.Project;

import java.io.File;

public class GlobalIndexSidebar extends ListView<File> {

    protected final IconData icon;
    protected final Project project;
    protected final ProjectGlobalIndex globalIndex;

    public GlobalIndexSidebar(Project project, ProjectGlobalIndex globalIndex) {
        this.project = project;
        this.globalIndex = globalIndex;

        setCellFactory(target -> new GlobalIndexSidebarElement(this));

        globalIndex.registerListeners(this, true);
        icon = Manager.get(FileTypeManager.class).getByExtension("asm").map(FileType::getIcon).orElse(null);

        getItems().addAll(globalIndex.getFiles());
    }

    /**
     * Returns the {@link Project} handling the files.
     *
     * @return the {@link Project}.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Returns the {@link ProjectGlobalIndex} this node is using.
     *
     * @return the {@link ProjectGlobalIndex}.
     */
    public ProjectGlobalIndex getGlobalIndex() {
        return globalIndex;
    }

    @Listener
    private void onFileAdd(FileCollectionAddFileEvent.After event) {
        getItems().add(event.getFile());
    }

    @Listener
    private void onFileRemoved(FileCollectionRemoveFileEvent.After event) {
        getItems().remove(event.getFile());
    }
}
