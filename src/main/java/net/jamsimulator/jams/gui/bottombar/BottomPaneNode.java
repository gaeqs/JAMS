package net.jamsimulator.jams.gui.bottombar;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents a wrapper of a node used by a {@link BottomBar}.
 * This wrapper has a {@link BottomPaneNodeHeader} that contains information
 * about the node.
 */
public class BottomPaneNode extends AnchorPane {

	private Node node;
	private BottomPaneNodeHeader header;

	/**
	 * Creates a bottom pane node.
	 *
	 * @param verticalSplitPane the vertical {@link SplitPane} where the node can be.
	 * @param node              the wrapped node.
	 * @param name              the name of the node.
	 */
	public BottomPaneNode(SplitPane verticalSplitPane, Node node, String name) {
		this.node = node;
		this.header = new BottomPaneNodeHeader(verticalSplitPane, name);

		AnchorUtils.setAnchor(header, 0, -1, 0, 0);
		AnchorUtils.setAnchor(node, BottomPaneNodeHeader.HEIGHT, 0, 0, 0);


		getChildren().add(header);
		getChildren().add(node);
	}

	/**
	 * Returns the {@link Node} wrapped by this side pane node.
	 *
	 * @return the {@link Node}.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Returns the {@link BottomPaneNodeHeader} of this side pane node.
	 *
	 * @return the {@link BottomPaneNodeHeader}.
	 */
	public BottomPaneNodeHeader getHeader() {
		return header;
	}
}
