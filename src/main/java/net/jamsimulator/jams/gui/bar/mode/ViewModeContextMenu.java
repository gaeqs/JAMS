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

package net.jamsimulator.jams.gui.bar.mode;

import javafx.scene.control.ContextMenu;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeRegisterEvent;
import net.jamsimulator.jams.gui.bar.mode.event.BarSnapshotViewModeUnregisterEvent;
import net.jamsimulator.jams.language.wrapper.LanguageCheckMenuItem;
import net.jamsimulator.jams.manager.Labeled;

import java.util.Comparator;

/**
 * Represents the {@link ContextMenu} showed when the user clicks a {@link net.jamsimulator.jams.gui.bar.BarButton}
 * with the secondary button. This menu allows the user to change the {@link BarSnapshotViewMode view mode} of the
 * button's snapshot.
 */
public class ViewModeContextMenu extends ContextMenu {

    private final BarSnapshot snapshot;

    /**
     * Creates the context menu.
     *
     * @param snapshot the {@link BarSnapshot snapshot} to manage.
     */
    public ViewModeContextMenu(BarSnapshot snapshot) {
        this.snapshot = snapshot;
        JamsApplication.getBarSnapshotViewModeManager().stream()
                .sorted((Comparator.comparing(Labeled::getName)))
                .forEach(mode -> getItems().add(new Item(mode)));

        JamsApplication.getBarSnapshotViewModeManager().registerListeners(this, true);

        setOnShowing(event -> getItems().forEach(item -> {
            if (item instanceof Item) ((Item) item)
                    .setSelected(this.snapshot.getViewMode().equals(((Item) item).viewMode));
        }));
    }

    @Listener
    private void onViewModeRegister(BarSnapshotViewModeRegisterEvent.After event) {
        getItems().clear();
        JamsApplication.getBarSnapshotViewModeManager().stream()
                .sorted((Comparator.comparing(Labeled::getName)))
                .forEach(mode -> getItems().add(new Item(mode)));
    }

    @Listener
    private void onViewModeUnregister(BarSnapshotViewModeUnregisterEvent.After event) {
        getItems().removeIf(item -> item instanceof Item && ((Item) item).viewMode.equals(event.getViewMode()));
    }

    /**
     * Small class made to manage a {@link BarSnapshotViewMode} change.
     */
    private class Item extends LanguageCheckMenuItem {

        final BarSnapshotViewMode viewMode;

        public Item(BarSnapshotViewMode viewMode) {
            super(viewMode.getLanguageNode());
            this.viewMode = viewMode;

            setOnAction(event -> Jams.getMainConfiguration().set(
                    String.format(BarSnapshot.CONFIGURATION_NODE_VIEW_MODE, snapshot.getName()),
                    viewMode.getName()));
        }
    }

}
