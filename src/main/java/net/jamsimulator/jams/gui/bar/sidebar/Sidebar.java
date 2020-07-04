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

package net.jamsimulator.jams.gui.bar.sidebar;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.bar.ProjectBar;
import net.jamsimulator.jams.gui.bar.ProjectBarPane;
import net.jamsimulator.jams.gui.bar.ProjectPaneSnapshot;

import java.util.Optional;

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
public class Sidebar extends VBox implements ProjectBar {

	private final boolean left;
	private final boolean top;

	private final SidePane sidePane;

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

	@Override
	public Optional<ProjectBarPane> getCurrent() {
		return Optional.ofNullable(top ? sidePane.getTop() : sidePane.getBottom());
	}

	/**
	 * Returns the {@link SidebarButton} that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the {@link SidebarButton}, if present.
	 */
	@Override
	public Optional<SidebarButton> get(String name) {
		return getChildren().stream().filter(target -> target instanceof SidebarButton
				&& ((SidebarButton) target).getName().equals(name))
				.map(target -> (SidebarButton) target).findAny();
	}

	@Override
	public boolean contains(String name) {
		return getChildren().stream().anyMatch(target -> target instanceof SidebarButton
				&& ((SidebarButton) target).getName().equals(name));
	}

	@Override
	public boolean add(ProjectPaneSnapshot snapshot) {
		if (contains(snapshot.getName())) return false;
		SidePaneNode sidePaneNode = new SidePaneNode(sidePane, top, snapshot);
		SidebarButton button = new SidebarButton(this, sidePaneNode, left);

		getChildren().add(button);

		return true;
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
			sidePane.setTop(button == null ? null : button.getPane());
		} else {
			sidePane.setBottom(button == null ? null : button.getPane());
		}
	}
}
