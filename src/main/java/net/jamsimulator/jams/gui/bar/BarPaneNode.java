package net.jamsimulator.jams.gui.bar;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Optional;

public class BarPaneNode extends AnchorPane {

    private final BarPaneNodeHeader header;

    private BarPaneSnapshot snapshot;

    public BarPaneNode(BarMap map, SplitPane splitPane) {
        header = new BarPaneNodeHeader(map, splitPane);
        AnchorUtils.setAnchor(header, 0, -1, 0, 0);
    }

    public Optional<BarPaneSnapshot> getSnapshot() {
        return Optional.ofNullable(snapshot);
    }

    public void selectSnapshot(BarPaneSnapshot snapshot) {
        this.snapshot = snapshot;
        header.selectSnapshot(snapshot);
        if (snapshot == null) {
            getChildren().clear();
        } else {
            AnchorUtils.setAnchor(snapshot.getNode(), BarPaneNodeHeader.HEIGHT, 0, 0, 0);
            getChildren().setAll(header, snapshot.getNode());
        }
    }

}
