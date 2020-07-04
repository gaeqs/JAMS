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

package net.jamsimulator.jams.gui.bar.bottombar;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.bar.ProjectBar;
import net.jamsimulator.jams.gui.bar.ProjectBarPane;
import net.jamsimulator.jams.gui.bar.ProjectPaneSnapshot;
import net.jamsimulator.jams.gui.bar.sidebar.SidePane;
import net.jamsimulator.jams.gui.bar.sidebar.SidePaneNode;
import net.jamsimulator.jams.gui.bar.sidebar.SidebarButton;

import java.util.Optional;

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
public class BottomBar extends HBox implements ProjectBar {

	private final SplitPane verticalSplitPane;
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

	@Override
	public Optional<BottomPaneNode> getCurrent() {
		return Optional.ofNullable(selected);
	}

	@Override
	public Optional<BottomBarButton> get(String name) {
		return getChildren().stream().filter(target -> target instanceof BottomBarButton
				&& ((BottomBarButton) target).getName().equals(name))
				.map(target -> (BottomBarButton) target).findAny();
	}

	@Override
	public boolean contains(String name) {
		return getChildren().stream().anyMatch(target -> target instanceof BottomBarButton
				&& ((BottomBarButton) target).getName().equals(name));
	}

	@Override
	public boolean add(ProjectPaneSnapshot snapshot) {
		if (contains(snapshot.getName())) return false;
		BottomPaneNode bottomPaneNode = new BottomPaneNode(verticalSplitPane, snapshot);
		BottomBarButton button = new BottomBarButton(this, bottomPaneNode, snapshot);

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
			SplitPane.setResizableWithParent(selected, false);
			verticalSplitPane.getItems().add(selected);
			verticalSplitPane.setDividerPosition(0, dividerPosition);
		}
	}
}
