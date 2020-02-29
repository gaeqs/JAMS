package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents a wrapper of a node used by a {@link Sidebar}.
 * This wrapper has a {@link SidePaneNodeHeader} that contains information
 * about the node.
 */
public class SidePaneNode extends AnchorPane {

	private Node node;
	private SidePaneNodeHeader header;

	/**
	 * Creates a side pane node.
	 *
	 * @param sidePane the {@link SidePane} handling this side pane node.
	 * @param node     the wrapped {@link Node}.
	 * @param name     the name of the {@link Node}.
	 * @param top      whether the {@link Sidebar} containing this node is a top {@link Sidebar}.
	 */
	public SidePaneNode(SidePane sidePane, Node node, String name, boolean top, String languageNode) {
		this.node = node;
		this.header = new SidePaneNodeHeader(sidePane, name, top, languageNode);

		AnchorUtils.setAnchor(header, 0, -1, 0, 0);
		AnchorUtils.setAnchor(node, SidePaneNodeHeader.HEIGHT, 0, 0, 0);


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
	 * Returns the {@link SidePaneNodeHeader} of this side pane node.
	 *
	 * @return the {@link SidePaneNodeHeader}.
	 */
	public SidePaneNodeHeader getHeader() {
		return header;
	}
}
