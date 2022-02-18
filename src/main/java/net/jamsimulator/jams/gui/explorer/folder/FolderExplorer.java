/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.file.FolderEventBroadcast;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents an {@link Explorer} whose elements
 * are the files and directories of a given {@link File folder}.
 */
public class FolderExplorer extends Explorer {

    private final File mainFolder;
    private final FolderEventBroadcast folderEventBroadcast;
    private Consumer<ExplorerFile> fileOpenAction;
    private BiConsumer<File, File> fileMoveAction;

    /**
     * Creates the explorer folder.
     *
     * @param mainFolder the main folder of the explorer.
     */
    public FolderExplorer(
            File mainFolder,
            FolderEventBroadcast folderEventBroadcast,
            ScrollPane scrollPane,
            Predicate<File> fileFilter
    ) {
        super(scrollPane, true, false);
        Validate.notNull(mainFolder, "Folder cannot be null!");
        Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");
        this.mainFolder = mainFolder;
        this.folderEventBroadcast = folderEventBroadcast;

        filter = element -> !(element instanceof ExplorerFile) || fileFilter.test(((ExplorerFile) element).getFile());

        fileOpenAction = file -> {
        };

        fileMoveAction = (a, b) -> {
        };

        generateMainSection();
        refreshWidth();
    }

    /**
     * Returns the main {@link File folder} of this explorer.
     *
     * @return the main {@link File folder}.
     */
    public File getMainFolder() {
        return mainFolder;
    }

    /**
     * Returns the {@link FolderEventBroadcast} this explorer uses to listen to folder updates.
     *
     * @return the {@link FolderEventBroadcast}.
     */
    public FolderEventBroadcast getFolderEventBroadcast() {
        return folderEventBroadcast;
    }

    /**
     * Returns the {@link ExplorerFile} represented by the given {@link File}.
     *
     * @param file the {@link File}.
     * @return the {@link ExplorerFile} representing this file, if present.
     */
    public Optional<ExplorerFile> getExplorerFile(File file) {
        return ((ExplorerFolder) mainSection).getExplorerFile(file);
    }

    /**
     * Returns the {@link ExplorerFolder} represented by the given {@link File}.
     *
     * @param file the {@link File folder}.
     * @return the {@link ExplorerFolder} representing this file, if present.
     */
    public Optional<ExplorerFolder> getExplorerFolder(File file) {
        return ((ExplorerFolder) mainSection).getExplorerFolder(file);
    }

    /**
     * Returns the {@link Consumer action} to perform when a file is double-clicked.
     *
     * @return the {@link Consumer action}.
     */
    public Consumer<ExplorerFile> getFileOpenAction() {
        return fileOpenAction;
    }

    /**
     * Sets the {@link Consumer action} to perform when a file is double-clicked.
     *
     * @param fileOpenAction the {@link Consumer action}.
     */
    public void setFileOpenAction(Consumer<ExplorerFile> fileOpenAction) {
        this.fileOpenAction = fileOpenAction;
    }

    /**
     * Returns the {@link BiConsumer action} to perform when a file is moved using the drag-and-drop feature.
     *
     * @return the {@link BiConsumer action}.
     */
    public BiConsumer<File, File> getFileMoveAction() {
        return fileMoveAction;
    }

    /**
     * Sets the {@link BiConsumer action} to perform when a file is moved using the drag-and-drop feature.
     *
     * @param fileMoveAction the {@link BiConsumer action}.
     */
    public void setFileMoveAction(BiConsumer<File, File> fileMoveAction) {
        this.fileMoveAction = fileMoveAction;
    }

    @Override
    protected void generateMainSection() {
        this.mainSection = new ExplorerFolder(this, null, mainFolder, 0);
        getChildren().add(this.mainSection);
    }
}
