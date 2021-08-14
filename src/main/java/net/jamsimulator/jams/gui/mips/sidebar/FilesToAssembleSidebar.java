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
import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.project.FilesToAssemble;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;

import java.io.File;

public class FilesToAssembleSidebar extends ListView<File> {

    protected final IconData icon;
    protected final Project project;
    protected final FilesToAssemble filesToAssemble;

    public FilesToAssembleSidebar(Project project, FilesToAssemble filesToAssemble, ScrollPane scrollPane) {
        this.project = project;
        this.filesToAssemble = filesToAssemble;

        setCellFactory(target -> new FilesToAssembleSidebarElement(this));

        filesToAssemble.registerListeners(this, true);
        icon = Jams.getFileTypeManager().getByExtension("asm").map(FileType::getIcon).orElse(null);

        getItems().addAll(filesToAssemble.getFiles());
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
     * Returns the {@link FilesToAssemble} this node is using.
     *
     * @return the {@link FilesToAssemble}.
     */
    public FilesToAssemble getFilesToAssemble() {
        return filesToAssemble;
    }

    @Listener
    private void onFileAdd(FileAddToAssembleEvent.After event) {
        getItems().add(event.getFile());
    }

    @Listener
    private void onFileRemoved(FileRemoveFromAssembleEvent.After event) {
        getItems().remove(event.getFile());
    }
}
