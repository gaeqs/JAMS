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
