package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.bar.mode.ViewModeContextMenu;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a button containing a {@link BarSnapshot}.
 * Buttons should be inside a {@link Bar}.
 */
public class BarButton extends ToggleButton {

    public static final int IMAGE_SIZE = 16;
    public static final String DRAG_DROP_PREFIX = "sidebar";

    private final Bar bar;
    private final BarSnapshot snapshot;

    private BarSnapshotHolder holder;

    /**
     * Creates the button.
     *
     * @param bar      the {@link Bar} containing this button.
     * @param snapshot the {@link BarSnapshot snapshot} being represented by the button.
     */
    public BarButton(Bar bar, BarSnapshot snapshot) {
        Validate.notNull(bar, "Bar cannot be null!");
        Validate.notNull(snapshot, "Snapshot cannot be null!");
        this.bar = bar;
        this.snapshot = snapshot;
        this.holder = null;

        snapshot.setButton(this);

        getStyleClass().add("bar-button");
        var label = snapshot.getLanguageNode()
                .map(v -> (Label) new LanguageLabel(v))
                .orElseGet(() -> new Label(snapshot.getName()));

        var group = new Group(label);

        if (snapshot.getIcon().isPresent()) {
            var view = new NearestImageView(snapshot.getIcon().get(), IMAGE_SIZE, IMAGE_SIZE);
            var pane = loadGroupPane();

            if (bar.getPosition() == BarPosition.RIGHT_TOP || bar.getPosition() == BarPosition.RIGHT_BOTTOM) {
                pane.getChildren().addAll(group, view);
            } else {
                pane.getChildren().addAll(view, group);
            }

            setGraphic(pane);
        } else {
            setGraphic(group);
        }

        setPrefWidth(WorkingPane.SIDEBAR_WIDTH);

        loadSelectListener();
        loadDragAndDropListeners();

        setContextMenu(new ViewModeContextMenu(snapshot));
    }

    /**
     * Returns the {@link Bar} containing this button.
     *
     * @return the {@link Bar}.
     */
    public Bar getBar() {
        return bar;
    }

    /**
     * Returns the {@link BarSnapshot snapshot} represented by this button.
     *
     * @return the {@link BarSnapshot snapshot}.
     */
    public BarSnapshot getSnapshot() {
        return snapshot;
    }

    /**
     * If the {@link BarSnapshot snapshot} of this button if being displayed,
     * returns the {@link BarSnapshotHolder holder} displaying it.
     *
     * @return the {@link BarSnapshotHolder holder} if the {@link BarSnapshot snapshot} is visible.
     */
    public Optional<BarSnapshotHolder> getCurrentHolder() {
        return Optional.ofNullable(holder);
    }

    /**
     * Makes visible the {@link BarSnapshot} being represented.
     *
     * @return whether this operation was successful.
     */
    public boolean show() {
        if (holder != null) return false;
        var newHolder = snapshot.getViewMode().manageView(this);
        newHolder.ifPresent(target -> holder = target);
        setSelected(newHolder.isPresent());
        return newHolder.isPresent();
    }

    /**
     * Hides the {@link BarSnapshot} being represented.
     *
     * @return whether this operation was successful.
     */
    public boolean hide() {
        if (holder == null) return false;
        if (holder.hide(this)) {
            setSelected(false);
            holder = null;
            return true;
        }
        return false;
    }

    /**
     * Marks this button as not selected, without
     * doing any management about the closure of the {@link BarSnapshot snapshot}.
     */
    public void forceHide() {
        holder = null;
        setSelected(false);
    }

    private Pane loadGroupPane() {
        return switch (bar.getPosition().getOrientation()) {
            case HORIZONTAL -> {
                var box = new HBox();
                box.setSpacing(4);
                box.setAlignment(Pos.CENTER);
                yield box;
            }
            case VERTICAL -> {
                var box = new VBox();
                box.setSpacing(4);
                box.setAlignment(Pos.CENTER);
                yield box;
            }
        };
    }

    private void loadSelectListener() {
        setOnMouseClicked(event -> {
            if (isSelected()) show();
            else hide();
        });
    }

    private void loadDragAndDropListeners() {
        addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!event.getDragboard().hasString() || !event.getDragboard().getString().startsWith(DRAG_DROP_PREFIX))
                return;
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            String name = event.getDragboard().getString().substring(DRAG_DROP_PREFIX.length() + 1);
            bar.manageDrop(name, bar.getNode().getChildren().indexOf(this));
            event.setDropCompleted(true);
            event.consume();
        });

        addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putString(DRAG_DROP_PREFIX + ":" + snapshot.getName());
            dragboard.setContent(content);

            event.consume();
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarButton barButton = (BarButton) o;
        return snapshot.equals(barButton.snapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snapshot);
    }
}
