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

package net.jamsimulator.jams.gui.editor;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.file.FileEvent;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FileEditorTab extends Tab implements ActionRegion {

    private final File file;
    private final FileEditor display;
    private final Label name;
    private final AnchorPane anchorPane, topAnchorPane;
    private FileEditorTabList list;
    private boolean saveMark;


    private final ChangeListener<Number> topNodeHeightListener;
    private FileEditorTabTopNode topNode;

    public FileEditorTab(FileEditorTabList list, File file) {
        this.list = list;
        this.file = file;
        this.saveMark = false;

        FileType type = Jams.getFileTypeManager().getByFile(file).orElse(Jams.getFileTypeManager().getUnknownType());
        this.display = type.createDisplayTab(this);

        var view = new QualityImageView(type.getIcon(), FileType.IMAGE_SIZE, FileType.IMAGE_SIZE);
        name = new Label(file.getName());

        var hbox = new HBox(view, name);
        hbox.setSpacing(5);
        setGraphic(hbox);

        if (display == null) {
            topAnchorPane = null;
            anchorPane = null;
            topNodeHeightListener = null;
            return;
        }

        topAnchorPane = new AnchorPane();
        anchorPane = new AnchorPane();

        topNodeHeightListener = (obs, old, val) ->
                AnchorUtils.setAnchor(anchorPane, val.doubleValue(), 0, 0, 0);

        AnchorUtils.setAnchor(anchorPane, 0, 0, 0, 0);
        topAnchorPane.getChildren().add(anchorPane);
        display.addNodesToTab(anchorPane);

        setContent(topAnchorPane);

        setOnClosed(target -> {
            this.list.closeFileInternal(this, true);
            display.onClose();
        });

        setContextMenu(createContextMenu());

        // Hides the top bar when the escape key is pressed.
        topAnchorPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                setTopNode(null);
                event.consume();
            }
        });

        // Register the file listener.
        list.getWorkingPane().getProjectTab().registerListeners(this, true);
    }

    public FileEditorTabList getList() {
        return list;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    void setList(FileEditorTabList list) {
        this.list = list;
    }

    public WorkingPane getWorkingPane() {
        return list.getWorkingPane();
    }

    public File getFile() {
        return file;
    }

    public FileEditor getDisplay() {
        return display;
    }

    public boolean isSaveMark() {
        return saveMark;
    }

    public Optional<FileEditorTabTopNode> getTopNode() {
        return Optional.ofNullable(topNode);
    }

    public void setSaveMark(boolean saveMark) {
        if (saveMark == this.saveMark) return;
        this.saveMark = saveMark;
        Platform.runLater(() -> name.setText(saveMark ? file.getName() + " *" : file.getName()));
    }

    public void openInNewHolder(FileOpenPosition position) {
        list.getHolder().openInNewHolder(this, position);
    }

    public void layoutDisplay() {
        list.requestLayout();
        if (anchorPane != null) anchorPane.requestLayout();
        ((Region) getContent()).requestLayout();
        ((Region) getGraphic()).requestLayout();
    }

    public void setTopNode(FileEditorTabTopNode value) {
        if (value == topNode || !(value instanceof Node) && value != null) return;
        if (topNode != null) {
            topAnchorPane.getChildren().remove((Node) topNode);
            AnchorUtils.setAnchor(anchorPane, 0, 0, 0, 0);

            if (topNode instanceof Region region) {
                region.heightProperty().removeListener(topNodeHeightListener);
            }

            topNode.onHide();
        }
        topNode = value;
        if (value != null) {
            topAnchorPane.getChildren().add((Node) value);
            AnchorUtils.setAnchor((Node) value, 0, -1, 0, 0);

            if (value instanceof Region region) {
                AnchorUtils.setAnchor(anchorPane, region.getHeight(), 0, 0, 0);
                region.heightProperty().addListener(topNodeHeightListener);
            } else {
                AnchorUtils.setAnchor(anchorPane, 30, 0, 0, 0);
            }

            value.onShow();
        }
    }

    private Set<ContextAction> getSupportedContextActions() {
        Set<Action> actions = JamsApplication.getActionManager();
        Set<ContextAction> set = new HashSet<>();
        for (Action action : actions) {
            if (action instanceof ContextAction && supportsActionRegion(action.getRegionTag())) {
                set.add((ContextAction) action);
            }
        }
        return set;
    }

    private ContextMenu createContextMenu() {
        Set<ContextAction> set = getSupportedContextActions();
        if (set.isEmpty()) return null;
        return new ContextActionMenuBuilder(this).addAll(set).build();
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.EDITOR_TAB.equals(region);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEditorTab that = (FileEditorTab) o;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }


    @Listener
    private void onFileChange(FileEvent event) {
        if (event.getWatchEvent().kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            if (!this.file.exists()) {
                setOnClosed(null);
                list.closeFileInternal(this, false);
                list.getTabs().remove(this);
                display.onClose();
            }
        }
    }
}
