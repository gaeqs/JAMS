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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Represents a sidebar. A sidebar is a small vertical rectangle situated at a side of the window.
 * This sidebar contains {@link SidebarButton}s that add or remove their assigned panes into this sidebar's {@link SidePane}.
 * <p>
 * The added panes will be wrapped inside {@link SidePaneNode}s.
 *
 * @see SidePane
 * @see SidebarButton
 * @see SidePaneNode
 */
public class Sidebar extends VBox {

	private boolean left;
	private boolean top;

	private SidePane sidePane;

	/**
	 * Creates a sidebar.
	 *
	 * @param left     whether this is a left or right sidebar.
	 * @param top      whether this is a top or bottom sidebar.
	 * @param sidePane the handled {@link SidePane}.
	 */
	public Sidebar(boolean left, boolean top, SidePane sidePane) {
		this.left = left;
		this.top = top;
		this.sidePane = sidePane;
		getStyleClass().addAll("sidebar", left ? "sidebar-left" : "sidebar-right");

		if (!top) setAlignment(Pos.BOTTOM_LEFT);
	}

	/**
	 * Returns the handled {@link SidePane}.
	 *
	 * @return the {@link SidePane}.
	 */
	public SidePane getSidePane() {
		return sidePane;
	}

	/**
	 * Returns whether this is a left sidebar.
	 *
	 * @return whether this is a left sidebar.
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * Returns whether this is a top sidebar.
	 *
	 * @return whether this is a top sidebar.
	 */
	public boolean isTop() {
		return top;
	}


	/**
	 * Returns whether this sidebar contains a node whose assigned name equals the given name.
	 *
	 * @param name the given name.
	 * @return whether this sidebar contains the node.
	 */
	public boolean containsNode(String name) {
		return getChildren().stream().anyMatch(target -> target instanceof SidebarButton
				&& ((SidebarButton) target).getName().equals(name));
	}

	/**
	 * Adds a node in this sidebar. This node will be wrapped by a {@link SidePaneNode}.
	 * <p>
	 * If a node with the given name is already inside the sidebar the given node wont be added.
	 *
	 * @param name the node name.
	 * @param node the given node.
	 * @param icon the icon shown on the button, or null.
	 * @return whether the given node was added.
	 */
	public boolean addNode(String name, Node node, Image icon, String languageNode) {
		if (containsNode(name)) return false;
		SidePaneNode sidePaneNode = new SidePaneNode(sidePane, node, name, top, languageNode);
		SidebarButton button = new SidebarButton(this, name, sidePaneNode, left, icon, languageNode);

		getChildren().add(button);

		return true;
	}

	/**
	 * Returns the selected node of the assigned {@link SidePane}.
	 *
	 * @return the selected node.
	 */
	SidePaneNode getSelected() {
		return top ? sidePane.getTop() : sidePane.getBottom();
	}

	/**
	 * Deselects all buttons except the given one.
	 *
	 * @param except the given button.
	 */
	void deselectExcept(SidebarButton except) {
		for (Node child : getChildren()) {
			if (except == child) continue;
			if (child instanceof SidebarButton)
				((SidebarButton) child).setSelected(false);
		}
	}

	/**
	 * Selects the given button, adding its {@link SidePaneNode} into the handled {@link SidePane}.
	 *
	 * @param button the button.
	 */
	void select(SidebarButton button) {
		if (button != null) deselectExcept(button);
		if (top) {
			sidePane.setTop(button == null ? null : button.getNode());
		} else {
			sidePane.setBottom(button == null ? null : button.getNode());
		}
	}
}
