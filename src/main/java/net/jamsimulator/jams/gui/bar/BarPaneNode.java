package net.jamsimulator.jams.gui.bar;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Optional;

public class BarPaneNode extends AnchorPane {

    private final BarPaneNodeHeader header;
    private BarButton button;

    public BarPaneNode(BarMap map, SplitPane splitPane) {
        header = new BarPaneNodeHeader(map, splitPane);
        AnchorUtils.setAnchor(header, 0, -1, 0, 0);
    }

    public Optional<BarButton> getButton() {
        return Optional.ofNullable(button);
    }

    public void selectButton(BarButton button) {
        if (this.button != null) {
            this.button.setSelected(false);
        }

        this.button = button;
        if (button == null) {
            header.selectSnapshot(null);
            getChildren().clear();
        } else {
            header.selectSnapshot(button.getSnapshot());
            AnchorUtils.setAnchor(button.getSnapshot().getNode(), BarPaneNodeHeader.HEIGHT, 0, 0, 0);
            getChildren().setAll(header, button.getSnapshot().getNode());
        }
    }

}
