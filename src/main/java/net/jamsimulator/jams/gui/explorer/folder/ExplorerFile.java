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

package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.input.*;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ExplorerFile extends ExplorerBasicElement {

    private final File file;
    private FileType type;

    /**
     * Creates an explorer file.
     *
     * @param parent         the {@link ExplorerSection} containing this file.
     * @param file           the represented {@link File}.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public ExplorerFile(ExplorerFolder parent, File file, int hierarchyLevel) {
        super(parent, file.getName(), hierarchyLevel);
        getStyleClass().add("explorer-file");
        this.file = file;

        type = Manager.get(FileTypeManager.class).getByFile(file).orElse(Manager.get(FileTypeManager.class).getUnknownType());
        icon.setIcon(type.getIcon());

        Manager.of(FileType.class).registerListeners(this, true);
    }

    /**
     * Returns the {@link File} represented by this explorer file.
     *
     * @return the {@link File}.
     */
    public File getFile() {
        return file;
    }

    @Override
    protected void onMouseClicked(MouseEvent mouseEvent) {
        super.onMouseClicked(mouseEvent);
        if (mouseEvent.getClickCount() % 2 == 0) {
            Explorer explorer = getExplorer();
            if (explorer instanceof FolderExplorer) {
                ((FolderExplorer) explorer).getFileOpenAction().accept(this);
                mouseEvent.consume();
            }
        }
    }

    @Override
    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Explorer explorer = getExplorer();
            if (explorer instanceof FolderExplorer) {
                ((FolderExplorer) explorer).getFileOpenAction().accept(this);
                event.consume();
                return;
            }
        }
        super.onKeyPressed(event);
    }

    @Override
    protected void loadListeners() {
        super.loadListeners();

        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!(parent instanceof ExplorerFolder) || !event.getDragboard().hasFiles()) return;
            File to = ((ExplorerFolder) parent).getFolder();
            if (event.getDragboard().getFiles().stream().anyMatch(target -> target.equals(to)
                    || FileUtils.isChild(to, target)))
                return;

            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            if (!getStyleClass().contains("explorer-file-allow-drop")) {
                getStyleClass().add("explorer-file-allow-drop");
            }
            getParentSection().ifPresent(ExplorerFolder::addDropHereHint);
            if (parent instanceof ExplorerFolder) ((ExplorerFolder) parent).removeDragHint();
            event.consume();
        });

        addEventHandler(DragEvent.DRAG_EXITED, event -> {
            getStyleClass().remove("explorer-file-allow-drop");
            getParentSection().ifPresent(ExplorerFolder::removeDropHereHint);
            applyCss();
        });


        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            var explorer = (FolderExplorer) getExplorer();
            FolderExplorerDragAndDropManagement.manageDrop(event.getDragboard(),
                    ((ExplorerFolder) parent).getFolder(), explorer.getFileMoveAction());
            event.setDropCompleted(true);
            event.consume();
        });

        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            if (!selected) {
                getExplorer().selectElementAlone(this);
            }
            Dragboard db = startDragAndDrop(TransferMode.COPY_OR_MOVE);
            List<ExplorerElement> selectedElements = getExplorer().getSelectedElements();
            FolderExplorerDragAndDropManagement.manageDragFromElements(db, selectedElements);
            event.consume();
        });
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return super.supportsActionRegion(region) || RegionTags.FOLDER_EXPLORER_ELEMENT.contains(region);
    }

    @Override
    public Optional<? extends ExplorerFolder> getParentSection() {
        return super.getParentSection().map(target -> (ExplorerFolder) target);
    }

    @Listener
    private void onFileTypeRegister(ManagerElementRegisterEvent.Before<FileType> event) {
        if (type != Manager.get(FileTypeManager.class).getUnknownType()) return;
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex == -1 || lastIndex == name.length()) return;

        if (event.getElement().supportsExtension(name.substring(lastIndex + 1))) {
            type = event.getElement();
            icon.setIcon(type.getIcon());
        }
    }

    @Listener
    private void onFileTypeUnregister(ManagerElementUnregisterEvent.Before<FileType> event) {
        if (type != event.getElement()) return;
        type = Manager.get(FileTypeManager.class).getByFile(file).orElse(Manager.get(FileTypeManager.class).getUnknownType());
        icon.setIcon(type.getIcon());
    }
}
