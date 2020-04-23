/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

	private final Node node;
	private final SidePaneNodeHeader header;

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
