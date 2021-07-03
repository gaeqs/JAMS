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

package net.jamsimulator.jams.gui.project;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.explorer.event.ExplorerAddElementEvent;
import net.jamsimulator.jams.gui.explorer.event.ExplorerRemoveElementEvent;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.project.FilesToAssemblerHolder;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.project.mips.event.FileAddToAssembleEvent;
import net.jamsimulator.jams.project.mips.event.FileRemoveFromAssembleEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Represents a explorer linked to a {@link Project}. These explorers can manager files to assemble and
 * automatically hides the project's metadata folder.
 */
public class ProjectFolderExplorer extends FolderExplorer {

    public static final String EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS = "mips-explorer-file-to-assemble";
    public static final String EXPLORER_OUT_FILE_STYLE_CLASS = "explorer-out-folder";

    private final Project project;
    private final FilesToAssemblerHolder holder;

    /**
     * Creates the mips explorer folder.
     *
     * @param project    the {@link MIPSProject} of this explorer.
     * @param scrollPane the {@link ScrollPane} handling this explorer.
     */
    public ProjectFolderExplorer(Project project, FilesToAssemblerHolder holder, ScrollPane scrollPane) {
        super(project.getFolder(), scrollPane,
                file -> !file.toPath().startsWith(project.getData().getMetadataFolder().toPath()));
        this.project = project;
        this.holder = holder;

        if (holder != null) {
            holder.getFilesToAssemble().registerListeners(this, true);
            for (File file : holder.getFilesToAssemble().getFiles()) {
                markFileToAssemble(file);
            }

            setFileMoveAction((from, to) -> {
                if (holder.getFilesToAssemble().containsFile(from)) {
                    Platform.runLater(() -> holder.getFilesToAssemble().addFile(to, true));
                }
            });
        }

        try {
            Files.walk(project.getData().getFilesFolder().toPath()).forEach(file -> markOutFile(file.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerListeners(this, true);
    }

    /**
     * Returns the {@link Project} of this explorer.
     *
     * @return the {@link Project}.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Disposes this project. This method must be called when this explorer is no longer needed.
     */
    public void dispose() {
        killWatchers();
    }

    private void markFileToAssemble(File file) {
        Optional<ExplorerFile> optional = getExplorerFile(file);
        if (optional.isEmpty()) return;
        ExplorerFile explorerFile = optional.get();

        if (!explorerFile.getStyleClass().contains(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS)) {
            explorerFile.getStyleClass().add(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
        }
    }

    private void markOutFile(File file) {
        Region region;
        var optional = getExplorerFile(file);
        if (optional.isEmpty()) {
            var folderOptional = getExplorerFolder(file);
            if (folderOptional.isEmpty()) return;
            region = folderOptional.get().getRepresentation();
        } else {
            region = optional.get();
        }

        if (!region.getStyleClass().contains(EXPLORER_OUT_FILE_STYLE_CLASS)) {
            region.getStyleClass().add(EXPLORER_OUT_FILE_STYLE_CLASS);
        }
    }

    private void unmarkFileToAssemble(File file) {
        Optional<ExplorerFile> optional = getExplorerFile(file);
        if (optional.isEmpty()) return;
        ExplorerFile explorerFile = optional.get();

        explorerFile.getStyleClass().remove(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
    }

    @Listener
    private void onFileAddedToAssemble(FileAddToAssembleEvent.After event) {
        File file = event.getFile();
        markFileToAssemble(file);
    }

    @Listener
    private void onFileRemoveFromAssemble(FileRemoveFromAssembleEvent.After event) {
        File file = event.getFile();
        unmarkFileToAssemble(file);
    }

    @Listener
    private void onFileAdded(ExplorerAddElementEvent.After event) {
        if (event.getElement() instanceof ExplorerFile eFile) {
            var file = eFile.getFile();

            if (holder != null && holder.getFilesToAssemble().containsFile(file)) {
                if (!eFile.getStyleClass().contains(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS)) {
                    eFile.getStyleClass().add(EXPLORER_FILE_TO_ASSEMBLE_STYLE_CLASS);
                }
            }

            if (file.toPath().toAbsolutePath()
                    .startsWith(project.getData().getFilesFolder().toPath().toAbsolutePath())) {
                ((Region) event.getElement()).getStyleClass().add(EXPLORER_OUT_FILE_STYLE_CLASS);
            }
        } else if (event.getElement() instanceof ExplorerFolder) {
            var file = ((ExplorerFolder) event.getElement()).getFolder();
            if (file.toPath().toAbsolutePath()
                    .startsWith(project.getData().getFilesFolder().toPath().toAbsolutePath())) {
                ((ExplorerFolder) event.getElement()).getRepresentation()
                        .getStyleClass().add(EXPLORER_OUT_FILE_STYLE_CLASS);
            }
        }
    }

    @Listener
    private void onFileRemoved(ExplorerRemoveElementEvent.After event) {
        if (holder != null) {
            holder.getFilesToAssemble().checkFiles();
        }
    }
}
