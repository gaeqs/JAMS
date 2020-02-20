package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;

public class SidePaneNode extends AnchorPane {

	private Node node;
	private SidePaneNodeHeader header;

	public SidePaneNode(SidePane sidePane, Node node, String name, boolean top) {
		this.node = node;
		this.header = new SidePaneNodeHeader(sidePane, name, top);

		AnchorUtils.setAnchor(header, 0, -1, 0, 0);
		AnchorUtils.setAnchor(node, SidePaneNodeHeader.HEIGHT, 0, 0, 0);


		getChildren().add(header);
		getChildren().add(node);
	}

	public Node getNode() {
		return node;
	}

	public SidePaneNodeHeader getHeader() {
		return header;
	}
}
