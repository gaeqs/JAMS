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

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import net.jamsimulator.jams.gui.project.WorkingPane;

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
