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

package net.jamsimulator.jams.gui.bar;

import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an editor bar that contains several {@link BarButton}s.
 * <p>
 * Bars must be stored inside a {@link BarMap}. This allows bars
 * to share buttons.
 */
public class Bar {

    private final BarMap map;
    private final Pane node;
    private final BarPosition position;
    private final BarPane barPane;

    private final Set<BarButton> buttons;

    /**
     * Creates the bar.
     *
     * @param map      the {@link BarMap}.
     * @param position the {@link BarPosition position} where this bar is showed.
     * @param barPane  the {@link BarPane} where this bar will put their nodes.
     */
    public Bar(BarMap map, BarPosition position, BarPane barPane) {
        this.map = map;
        this.position = position;
        this.barPane = barPane;
        this.buttons = new HashSet<>();

        this.node = switch (position.getOrientation()) {
            case VERTICAL -> new VBox();
            case HORIZONTAL -> new HBox();
        };

        var classes = node.getStyleClass();
        classes.add("bar");
        position.addStyleClasses(classes);

        loadDragAndDropListeners();
    }

    /**
     * Returns the {@link BarMap} containing this bar.
     *
     * @return the {@link BarMap}.
     */
    public BarMap getMap() {
        return map;
    }

    /**
     * Returns the {@link Pane} of this bar.
     * <p>
     * This pane should be the representation of the bar, containing all enabled buttons.
     *
     * @return the {@link Pane}.
     */
    public Pane getNode() {
        return node;
    }

    /**
     * Returns the {@link BarPane} linked to this bar.
     * <p>
     * The {@link BarPane} is the place where {@link BarSnapshot}s will be represented
     * if they choose the {@link net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePane pane view mode}.
     *
     * @return the {@link BarPane}.
     */
    public BarPane getBarPane() {
        return barPane;
    }

    /**
     * Returns the {@link BarPosition position} this bar is located at.
     * <p>
     * This {@link BarPosition position} is used to identify this bar inside the {@link BarMap}.
     *
     * @return the {@link BarPosition position}.
     * @see #getBarPane()
     */
    public BarPosition getPosition() {
        return position;
    }

    /**
     * Adds the given {@link BarSnapshot snapshot} into this bar.
     * <p>
     * Snapshots from another {@link BarMap} SHOULD NOT be used in this method.
     * The given snapshot should be registered in the {@link BarMap} for this method to work properly.
     *
     * @param snapshot the {@link BarSnapshot snapshot}.
     * @return whether the operation was successful.
     */
    public boolean add(BarSnapshot snapshot) {
        if (buttons.stream().anyMatch(target -> target.getSnapshot().equals(snapshot))) return false;
        var button = new BarButton(this, snapshot);
        buttons.add(button);
        node.getChildren().add(button);
        return true;
    }

    /**
     * Removes the given {@link BarSnapshot snapshot} from this bar.
     *
     * @param snapshot the {@link BarSnapshot snapshot}.
     * @return whether the operation was successful.
     */
    public boolean remove(BarSnapshot snapshot) {
        var button = buttons.stream().filter(target -> target.getSnapshot().equals(snapshot)).findAny();
        if (button.isEmpty()) return false;
        button.get().hide();
        buttons.remove(button.get());
        node.getChildren().remove(button.get());
        button.get().getSnapshot().setButton(null);

        return true;
    }

    /**
     * Returns whether this bar contains the given {@link BarSnapshot snapshot}.
     *
     * @param snapshot the {@link BarSnapshot snapshot}.
     * @return whether the bar contains the snapshot.
     */
    public boolean contains(BarSnapshot snapshot) {
        return buttons.stream().anyMatch(target -> target.getSnapshot().equals(snapshot));
    }

    /**
     * Returns an unmodifiable {@link Set} with all {@link BarButton}s inside this bar.
     *
     * @return the unmodifiable {@link Set}.
     * @see Collections#unmodifiableSet(Set)
     */
    public Set<BarButton> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    /**
     * Returns the {@link BarButton} representing the given {@link BarSnapshot snapshot} if present.
     *
     * @param snapshot the {@link BarSnapshot snapshot}.
     * @return the {@link BarButton} if present.
     */
    public Optional<BarButton> getButton(BarSnapshot snapshot) {
        return buttons.stream().filter(target -> target.getSnapshot().equals(snapshot)).findAny();
    }

    /**
     * Returns the {@link BarButton} representing the {@link BarSnapshot snapshot} that matches the given name if present.
     *
     * @param snapshotName the name of the {@link BarSnapshot snapshot}.
     * @return the {@link BarButton} if present.
     */
    public Optional<BarButton> getButton(String snapshotName) {
        return buttons.stream().filter(target -> target.getSnapshot().getName().equals(snapshotName)).findAny();
    }

    void manageDrop(String name, int index) {
        if (index < 0) index = 0;

        var optional = map.searchButton(name);
        if (optional.isEmpty()) return;
        var button = optional.get();

        if (button.getBar().equals(this)) {
            node.getChildren().remove(button);
            node.getChildren().add(Math.min(node.getChildren().size(), index), button);
        } else {
            button.getBar().remove(button.getSnapshot());
            add(button.getSnapshot());
            // Leaves the management to the BarPaneSnapshot
            Jams.getMainConfiguration().data().set(String.format(BarSnapshot.CONFIGURATION_NODE_POSITION, name), position);
        }
    }


    private void loadDragAndDropListeners() {
        node.addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!event.getDragboard().hasString() ||
                    !event.getDragboard().getString().startsWith(BarButton.DRAG_DROP_PREFIX))
                return;
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        node.addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            String name = event.getDragboard()
                    .getString().substring(BarButton.DRAG_DROP_PREFIX.length() + 1);
            manageDrop(name, node.getChildren().size());
            event.setDropCompleted(true);
            event.consume();
        });
    }

}
