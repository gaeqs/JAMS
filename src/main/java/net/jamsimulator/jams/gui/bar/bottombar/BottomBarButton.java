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

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.bar.ProjectBarButton;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * Represents a button inside a {@link BottomBar}.
 */
public class BottomBarButton extends ToggleButton implements ProjectBarButton {

	public static final int IMAGE_SIZE = 16;

	private final BottomBar bottomBar;

	private final String name;
	private final BottomPaneNode node;

	/**
	 * Creates a bottom bar button.
	 *
	 * @param bottomBar the bottom bar handling this button.
	 * @param name      the name of this button.
	 * @param node      the node handled by this button.
	 */
	public BottomBarButton(BottomBar bottomBar, String name, BottomPaneNode node, Image icon, String languageNode) {
		this.bottomBar = bottomBar;

		this.name = name;
		this.node = node;

		getStyleClass().addAll("bottom-bar-button");

		Label label = languageNode == null ? new Label(name) : new LanguageLabel(languageNode);
		Group group = new Group(label);

		if (icon != null) {
			ImageView imageView = new NearestImageView(icon, IMAGE_SIZE, IMAGE_SIZE);
			HBox hBox = new HBox(imageView, group);
			hBox.setSpacing(4);
			hBox.setAlignment(Pos.CENTER);
			setGraphic(hBox);
		} else {
			setGraphic(group);
		}
		setPrefHeight(WorkingPane.SIDEBAR_WIDTH);

		selectedProperty().addListener((obs, old, val) -> {
			if (old == val) return;
			if (val) {
				bottomBar.select(this);
			} else if (bottomBar.getCurrent().orElse(null) == node) {
				bottomBar.select(null);
			}

		});

	}

	@Override
	public BottomBar getProjectBar() {
		return bottomBar;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link BottomPaneNode} handled by this button.
	 *
	 * @return the {@link BottomPaneNode}.
	 */
	public BottomPaneNode getNode
	() {
		return pane;
	}
}
