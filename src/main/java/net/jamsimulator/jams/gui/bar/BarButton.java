package net.jamsimulator.jams.gui.bar;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class BarButton extends ToggleButton {

    public static final int IMAGE_SIZE = 16;
    public static final String DRAG_DROP_PREFIX = "sidebar";

    private final Bar bar;
    private final BarPaneSnapshot snapshot;


    public BarButton(Bar bar, BarPaneSnapshot snapshot) {
        Validate.notNull(bar, "Bar cannot be null!");
        Validate.notNull(snapshot, "Snapshot cannot be null!");
        this.bar = bar;
        this.snapshot = snapshot;

        getStyleClass().add("bar-button");
        var label = snapshot.getLanguageNode() == null
                ? new Label(snapshot.getName())
                : new LanguageLabel(snapshot.getLanguageNode());

        var group = new Group(label);

        if (snapshot.getIcon() != null) {
            var view = new NearestImageView(snapshot.getIcon(), IMAGE_SIZE, IMAGE_SIZE);
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
    }

    public Bar getBar() {
        return bar;
    }

    public BarPaneSnapshot getSnapshot() {
        return snapshot;
    }

    public boolean show() {
        if (bar.show(this)) {
            setSelected(true);
            return true;
        }
        return false;
    }

    public boolean hide() {
        if (bar.hide(this)) {
            setSelected(false);
            return true;
        }
        return false;
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
        selectedProperty().addListener((obs, old, val) -> {
            if (old == val) return;
            if (val) bar.show(this);
            else bar.hide(this);
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
            bar.manageDrop(name, bar.getPane().getChildren().indexOf(this));
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
