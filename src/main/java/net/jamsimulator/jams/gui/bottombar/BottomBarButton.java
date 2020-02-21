package net.jamsimulator.jams.gui.bottombar;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import net.jamsimulator.jams.gui.main.WorkingPane;

/**
 * Represents a button inside a {@link BottomBar}.
 */
public class BottomBarButton extends ToggleButton {

	private BottomBar bottomBar;

	private String name;
	private BottomPaneNode node;

	/**
	 * Creates a bottom bar button.
	 *
	 * @param bottomBar the bottom bar handling this button.
	 * @param name      the name of this button.
	 * @param node      the node handled by this button.
	 */
	public BottomBarButton(BottomBar bottomBar, String name, BottomPaneNode node) {
		this.bottomBar = bottomBar;

		this.name = name;
		this.node = node;

		getStyleClass().addAll("bottom-bar-button");

		Label label = new Label(name);
		Group group = new Group(label);

		setGraphic(group);
		setPrefHeight(WorkingPane.SIDEBAR_WIDTH);

		selectedProperty().addListener((obs, old, val) -> {
			if (old == val) return;
			if (val) {
				bottomBar.select(this);
			} else if (bottomBar.getSelected() == node) {
				bottomBar.select(null);
			}

		});

	}

	/**
	 * Returns the {@link BottomBar} handling this button.
	 *
	 * @return the {@link BottomBar}.
	 */
	public BottomBar getBottomBar() {
		return bottomBar;
	}

	/**
	 * Returns the name of the button.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link BottomPaneNode} handled by this button.
	 *
	 * @return the {@link BottomPaneNode}.
	 */
	public BottomPaneNode getNode() {
		return node;
	}
}
