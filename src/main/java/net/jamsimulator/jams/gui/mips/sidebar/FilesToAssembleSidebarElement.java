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

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.DraggableListCell;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FilesToAssembleSidebarElement extends DraggableListCell<File> implements ActionRegion {

    private final FilesToAssembleSidebar display;

    public FilesToAssembleSidebarElement(FilesToAssembleSidebar display) {
        this.display = display;
        setOnContextMenuRequested(this::manageContextMenuRequest);
    }

    public FilesToAssembleSidebar getDisplay() {
        return display;
    }

    private Set<ContextAction> getSupportedContextActions() {
        var actions = JamsApplication.getActionManager();
        var set = new HashSet<ContextAction>();
        if (isEmpty() || getItem() == null) return set;
        for (Action action : actions) {
            if (action instanceof ContextAction contextAction && supportsActionRegion(action.getRegionTag())) {
                set.add(contextAction);
            }
        }
        return set;
    }

    private void manageContextMenuRequest(ContextMenuEvent request) {
        var set = getSupportedContextActions();
        if (set.isEmpty()) {
            request.consume();
            return;
        }
        ContextMenu main = new ContextActionMenuBuilder(this).addAll(set).build();
        JamsApplication.openContextMenu(main, this, request.getScreenX(), request.getScreenY());
        request.consume();
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_FILE_TO_ASSEMBLE.equals(region);
    }

    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) setGraphic(null);
        else {
            var hbox = new HBox();
            hbox.setSpacing(ExplorerBasicElement.SPACING);
            hbox.getChildren().add(new QualityImageView(display.icon, FileType.IMAGE_SIZE, FileType.IMAGE_SIZE));
            hbox.getChildren().add(new Label(display.getProject().getFolder()
                    .toPath().relativize(item.toPath()).toString()));
            setGraphic(hbox);
        }
    }

    @Override
    protected void onDragDropped(DragEvent event) {
        if (getItem() == null) return;
        var dragboard = event.getDragboard();

        if (dragboard.hasString()) {
            var items = getListView().getItems();

            int index = Integer.parseInt(dragboard.getString());
            if (index < 0 || index >= items.size()) return;
            int to = getIndex();
            items.add(to, items.remove(index));
            display.getFilesToAssemble().moveFileToIndex(getItem(), to);
        }
        event.consume();
    }
}
