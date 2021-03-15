package net.jamsimulator.jams.gui.bar;

import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class Bar {

    private final BarMap map;
    private final Pane pane;
    private final BarPosition position;
    private final BarPane barPane;

    private final Set<BarButton> buttons;

    public Bar(BarMap map, BarPosition position, BarPane barPane) {
        this.map = map;
        this.position = position;
        this.barPane = barPane;
        this.buttons = new HashSet<>();

        this.pane = switch (position.getOrientation()) {
            case VERTICAL -> new VBox();
            case HORIZONTAL -> new HBox();
        };

        var classes = pane.getStyleClass();
        classes.add("bar");
        position.addStyleClasses(classes);

        loadDragAndDropListeners();
    }

    public BarMap getMap() {
        return map;
    }

    public Pane getPane() {
        return pane;
    }

    public BarPane getBarPane() {
        return barPane;
    }

    public BarPosition getPosition() {
        return position;
    }

    public boolean add(BarPaneSnapshot snapshot) {
        if (buttons.stream().anyMatch(target -> target.getSnapshot().equals(snapshot))) return false;
        var button = new BarButton(this, snapshot);
        buttons.add(button);
        pane.getChildren().add(button);
        return true;
    }

    public boolean remove(BarPaneSnapshot snapshot) {
        var button = buttons.stream().filter(target -> target.getSnapshot().equals(snapshot)).findAny();
        if (button.isEmpty()) return false;
        button.get().hide();
        buttons.remove(button.get());
        pane.getChildren().remove(button.get());

        return true;
    }

    public boolean contains(BarPaneSnapshot snapshot) {
        return buttons.stream().anyMatch(target -> target.getSnapshot().equals(snapshot));
    }

    public Set<BarButton> getButtons() {
        return Collections.unmodifiableSet(buttons);
    }

    public Optional<BarButton> getButton(BarPaneSnapshot snapshot) {
        return buttons.stream().filter(target -> target.getSnapshot().equals(snapshot)).findAny();
    }

    public Optional<BarButton> getButton(String snapshotName) {
        return buttons.stream().filter(target -> target.getSnapshot().getName().equals(snapshotName)).findAny();
    }

    boolean show(BarButton button) {
        if (position.shouldUseFirstPane()) {
            if (barPane.getFirstNode().getSnapshot().orElse(null) != button.getSnapshot()) {
                barPane.selectFirst(button.getSnapshot());
                return true;
            }
        } else {
            if (barPane.getSecondNode().getSnapshot().orElse(null) != button.getSnapshot()) {
                barPane.selectSecond(button.getSnapshot());
                return true;
            }
        }

        return false;
    }

    boolean hide(BarButton button) {
        if (position.shouldUseFirstPane()) {
            if (barPane.getFirstNode().getSnapshot().orElse(null) == button.getSnapshot()) {
                barPane.selectFirst(null);
                return true;
            }
        } else {
            if (barPane.getSecondNode().getSnapshot().orElse(null) == button.getSnapshot()) {
                barPane.selectSecond(null);
                return true;
            }
        }

        return false;
    }

    void manageDrop(String name, int index) {
        if (index < 0) index = 0;

        var optional = map.searchButton(name);
        if (optional.isEmpty()) return;
        var button = optional.get();

        if (button.getBar().equals(this)) {
            pane.getChildren().remove(button);
            pane.getChildren().add(Math.min(pane.getChildren().size(), index), button);
        } else {
            button.getBar().remove(button.getSnapshot());
            add(button.getSnapshot());
        }
    }


    private void loadDragAndDropListeners() {
        pane.addEventHandler(DragEvent.DRAG_OVER, event -> {
            if (!event.getDragboard().hasString() ||
                    !event.getDragboard().getString().startsWith(BarButton.DRAG_DROP_PREFIX))
                return;
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        pane.addEventHandler(DragEvent.DRAG_DROPPED, event -> {
            String name = event.getDragboard()
                    .getString().substring(BarButton.DRAG_DROP_PREFIX.length() + 1);
            manageDrop(name, pane.getChildren().size());
            event.setDropCompleted(true);
            event.consume();
        });
    }

}
