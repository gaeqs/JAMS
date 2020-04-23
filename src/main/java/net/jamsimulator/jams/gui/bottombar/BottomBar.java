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
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.SidePaneNode;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;

/**
 * Represents a bottom bar. A bottom bar is a small vertical rectangle situated at a side of the window.
 * This bottom bar contains {@link SidebarButton}s that add or remove their assigned panes into this bottom bar's {@link SidePane}.
 * <p>
 * The added panes will be wrapped inside {@link SidePaneNode}s.
 *
 * @see SidePane
 * @see SidebarButton
 * @see SidePaneNode
 */
public class BottomBar extends HBox {

	private SplitPane verticalSplitPane;
	private BottomPaneNode selected;

	private double dividerPosition;

	/**
	 * Creates a bottom bar.
	 *
	 * @param verticalSplitPane the handled {@link SplitPane}.
	 */
	public BottomBar(SplitPane verticalSplitPane) {
		this.verticalSplitPane = verticalSplitPane;
		this.selected = null;
		getStyleClass().addAll("bottom-bar");
		dividerPosition = 0.7;
	}

	/**
	 * Returns the handled {@link SplitPane}.
	 *
	 * @return the {@link SplitPane}.
	 */
	public SplitPane getVerticalSplitPane() {
		return verticalSplitPane;
	}

	/**
	 * Returns the selected {@link BottomPaneNode}, or null.
	 *
	 * @return the selected {@link BottomPaneNode}, or null.
	 */
	public BottomPaneNode getSelected() {
		return selected;
	}

	/**
	 * Returns whether this bottom bar contains a node whose assigned name equals the given name.
	 *
	 * @param name the given name.
	 * @return whether this bottom bar contains the node.
	 */
	public boolean containsNode(String name) {
		return getChildren().stream().anyMatch(target -> target instanceof BottomBarButton
				&& ((BottomBarButton) target).getName().equals(name));
	}

	/**
	 * Adds a node in this bottom bar. This node will be wrapped by a {@link SidePaneNode}.
	 * <p>
	 * If a node with the given name is already inside the bottom bar the given node wont be added.
	 *
	 * @param name the node name.
	 * @param node the given node.
	 * @return whether the given node was added.
	 */
	public boolean addNode(String name, Node node) {
		if (containsNode(name)) return false;
		BottomPaneNode bottomPaneNode = new BottomPaneNode(verticalSplitPane, node, name);
		BottomBarButton button = new BottomBarButton(this, name, bottomPaneNode);

		getChildren().add(button);

		return true;
	}

	/**
	 * Deselects all buttons except the given one.
	 *
	 * @param except the given button.
	 */
	void deselectExcept(BottomBarButton except) {
		for (Node child : getChildren()) {
			if (except == child) continue;
			if (child instanceof BottomBarButton)
				((BottomBarButton) child).setSelected(false);
		}
	}

	/**
	 * Selects the given button, adding its {@link SidePaneNode} into the handled {@link SidePane}.
	 *
	 * @param button the button.
	 */
	void select(BottomBarButton button) {

		if (selected != null) {
			dividerPosition = verticalSplitPane.getDividerPositions()[0];
			verticalSplitPane.getItems().remove(selected);
		}
		selected = button == null ? null : button.getNode();
		deselectExcept(button);
		if (selected != null) {
			verticalSplitPane.getItems().add(selected);
			verticalSplitPane.setDividerPosition(0, dividerPosition);
		}
	}
}
