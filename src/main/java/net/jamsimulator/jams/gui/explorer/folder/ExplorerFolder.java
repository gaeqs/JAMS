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

import javafx.application.Platform;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSectionRepresentation;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Represents a {@link File folder} inside an {@link Explorer}.
 */
public class ExplorerFolder extends ExplorerSection {

    private final File folder;
    protected WatchService service;
    protected boolean serviceRunning;

    /**
     * Creates the explorer folder.
     *
     * @param explorer       the {@link Explorer} of this folder.
     * @param parent         the {@link ExplorerSection} containing this folder. This may be null.
     * @param folder         the folder to represent.
     * @param hierarchyLevel the hierarchy level, used by the spacing.
     */
    public ExplorerFolder(Explorer explorer, ExplorerSection parent, File folder, int hierarchyLevel) {
        super(explorer, parent, folder.getName(), hierarchyLevel, ElementsComparator.INSTANCE);
        representation.getStyleClass().add("explorer-folder");
        this.folder = folder;
        loadChildren();
        loadWatcher();
        refreshAllElements();
    }

    /**
     * Returns the represented folder.
     *
     * @return the represented folder.
     */
    public File getFolder() {
        return folder;
    }

    /**
     * Returns the {@link ExplorerFile} represented by the given {@link File}.
     *
     * @param file the {@link File}.
     * @return the {@link ExplorerFile} representing this file, if present.
     */
    public Optional<ExplorerFile> getExplorerFile(File file) {
        if (folder.equals(file)) return Optional.empty();
        for (ExplorerElement element : elements) {
            if (element instanceof ExplorerFile) {
                if (((ExplorerFile) element).getFile().equals(file)) {
                    return Optional.of((ExplorerFile) element);
                }
            } else if (element instanceof ExplorerFolder) {
                Optional<ExplorerFile> optional = ((ExplorerFolder) element).getExplorerFile(file);
                if (optional.isPresent()) {
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the {@link ExplorerFolder} represented by the given {@link File}.
     *
     * @param file the {@link File folder}.
     * @return the {@link ExplorerFolder} representing this file, if present.
     */
    public Optional<ExplorerFolder> getExplorerFolder(File file) {
        if (folder.equals(file)) return Optional.of(this);
        for (ExplorerElement element : elements) {
            if (element instanceof ExplorerFolder) {
                Optional<ExplorerFolder> optional = ((ExplorerFolder) element).getExplorerFolder(file);
                if (optional.isPresent()) {
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Kills the {@link WatchService} of this folder. This prevents this folder
     * from receiving alerts from changes inside the folder.
     * <p>
     * This method should be used when the folder is not longer used.
     *
     * @throws IOException any exception thrown by {@link WatchService#close()}.
     */
    public void killWatchService() throws IOException {
        serviceRunning = false;
        service.close();

        for (ExplorerElement element : elements) {
            if (element instanceof ExplorerFolder)
                ((ExplorerFolder) element).killWatchService();
        }
    }

    /**
     * Adds the given file into this folderº explorer.
     * <p>
     * This method is used by the {@link WatchService}.
     *
     * @param file the file.
     */
    public void addFile(File file) {
        ExplorerElement element;
        if (file.isDirectory()) {
            element = new ExplorerFolder(explorer, this, file, hierarchyLevel + 1);
        } else {
            element = new ExplorerFile(this, file, hierarchyLevel + 1);
        }
        addElement(element);
    }


    /**
     * Removes the given file from this folder explorer.
     * <p>
     * This method is used by the {@link WatchService}.
     *
     * @param file the file.
     */
    public void removeFile(File file) {
        //Tries to remove a folder. If the folder doesn't exist, tries to remove a file.
        ExplorerFolder folder = (ExplorerFolder) elements.stream().filter(target ->
                target instanceof ExplorerFolder && ((ExplorerFolder) target).folder.equals(file))
                .findFirst().orElse(null);
        if (folder != null) {
            if (!removeElement(folder)) return;
            try {
                folder.killWatchService();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            elements.stream().filter(target ->
                    target instanceof ExplorerFile && ((ExplorerFile) target).getFile().equals(file))
                    .findFirst().ifPresent(this::removeElement);
        }
    }

    /**
     * Removes the drag hint from this element.
     */
    public void removeDragHint() {
        representation.getStyleClass().remove("explorer-folder-allow-drop");
        if (parent != null && parent instanceof ExplorerFolder) ((ExplorerFolder) parent).removeDragHint();
    }

    /**
     * Adds the drop here hint to this element.
     */
    public void addDropHereHint() {
        if (!representation.getStyleClass().contains("explorer-folder-drop-location")) {
            representation.getStyleClass().add("explorer-folder-drop-location");
        }
    }

    /**
     * Removes the drop here hint from this element.
     */
    public void removeDropHereHint() {
        representation.getStyleClass().remove("explorer-folder-drop-location");
    }

    @Override
    protected void loadListeners() {
        super.loadListeners();

        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!event.getDragboard().hasFiles()) return;
            if (event.getDragboard().getFiles().stream().anyMatch(target -> target.equals(folder)
                    || FileUtils.isChild(folder, target)))
                return;

            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            if (!representation.getStyleClass().contains("explorer-folder-allow-drop")) {
                representation.getStyleClass().add("explorer-folder-allow-drop");
            }
            event.consume();
        });

        addEventHandler(DragEvent.DRAG_EXITED, event -> removeDragHint());

        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            var explorer = (FolderExplorer) getExplorer();
            FolderExplorerDragAndDropManagement.manageDrop(event.getDragboard(), folder, explorer.getFileMoveAction());
            event.setDropCompleted(true);
            event.consume();
        });

        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            if (!isSelected()) {
                getExplorer().selectElementAlone(this);
            }
            Dragboard db = startDragAndDrop(TransferMode.COPY_OR_MOVE);
            List<ExplorerElement> selectedElements = getExplorer().getSelectedElements();
            FolderExplorerDragAndDropManagement.manageDragFromElements(db, selectedElements);
            event.consume();
        });
    }

    private void loadChildren() {
        File[] folderFiles = folder.listFiles();
        if (folderFiles == null) return;

        ExplorerElement element;
        for (File file : folderFiles) {
            if (file.isDirectory()) {
                element = new ExplorerFolder(explorer, this, file, hierarchyLevel + 1);
                addElement(element);
            } else if (file.isFile()) {
                element = new ExplorerFile(this, file, hierarchyLevel + 1);
                addElement(element);
            }
        }
        representation.refreshStatusIcon();
    }

    private void loadWatcher() {
        //Loads the watch service.
        try {
            service = FileSystems.getDefault().newWatchService();
            folder.toPath().register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            serviceRunning = true;

            new Thread(() -> {
                //While the folder is not killed, run the service.
                while (serviceRunning) {
                    try {
                        serviceRunning = processKey(service.take());
                    } catch (ClosedWatchServiceException ignore) {
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean processKey(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW) continue;

            WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;

            Path dir = (Path) key.watchable();
            Path path = dir.resolve(watchEvent.context());
            File file = path.toFile();

            if (kind == ENTRY_DELETE) {
                Platform.runLater(() -> removeFile(file));
            } else if (kind == ENTRY_CREATE) {
                Platform.runLater(() -> addFile(file));
            }
        }
        return key.reset();
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return super.supportsActionRegion(region) || RegionTags.FOLDER_EXPLORER_ELEMENT.contains(region);
    }

    @Override
    protected ExplorerSectionRepresentation loadRepresentation() {
        return new ExplorerFolderRepresentation(this, hierarchyLevel);
    }

    /**
     * This class is used to compare and sort all elements inside an {@link ExplorerFolder}.
     * {@link ExplorerFolder} have priority above all other {@link ExplorerElement}s.
     */
    private static class ElementsComparator implements Comparator<ExplorerElement> {

        public static final ElementsComparator INSTANCE = new ElementsComparator();

        private ElementsComparator() {
        }

        @Override
        public int compare(ExplorerElement o1, ExplorerElement o2) {
            if (o1 instanceof ExplorerFolder && !(o2 instanceof ExplorerFolder))
                return -1;
            if (!(o1 instanceof ExplorerFolder) && o2 instanceof ExplorerFolder)
                return 1;
            return o1.getName().compareTo(o2.getName());
        }
    }
}
