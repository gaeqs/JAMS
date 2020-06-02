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
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;


/**
 * Represents a button inside a {@link Sidebar}.
 */
public class SidebarButton extends ToggleButton {

	public static final int IMAGE_SIZE = 16;

	private final Sidebar sidebar;

	private final String name;
	private final SidePaneNode node;
	private Image image;

	/**
	 * Creates a sidebar button.
	 *
	 * @param sidebar the sidebar handling this button.
	 * @param name    the name of this button.
	 * @param node    the node handled by this button.
	 * @param left    whether the sidebar is a left sidebar or a right one.
	 * @param icon    the icon of the button, or null.
	 */
	public SidebarButton(Sidebar sidebar, String name, SidePaneNode node, boolean left, Image icon, String languageNode) {
		this.sidebar = sidebar;
		this.name = name;
		this.node = node;

		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");

		Label label = languageNode == null ? new Label(name) : new LanguageLabel(languageNode);
		Group group = new Group(label);

		if (icon != null) {
			ImageView imageView = new NearestImageView(icon, IMAGE_SIZE, IMAGE_SIZE);
			VBox vBox = left ? new VBox(group, imageView) : new VBox(imageView, group);
			vBox.setSpacing(2);
			vBox.setAlignment(Pos.CENTER);
			setGraphic(vBox);
		} else {
			setGraphic(group);
		}

		setPrefWidth(WorkingPane.SIDEBAR_WIDTH);

		selectedProperty().addListener((obs, old, val) -> {
			if (old == val) return;
			if (val) {
				sidebar.select(this);
			} else if (sidebar.getSelected() == node) {
				sidebar.select(null);
			}
		});
	}

	/**
	 * Returns the {@link Sidebar} handling this button.
	 *
	 * @return the {@link Sidebar}.
	 */
	public Sidebar getSidebar() {
		return sidebar;
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
	 * Returns the {@link SidePaneNode} handled by this button.
	 *
	 * @return the {@link SidePaneNode}.
	 */
	public SidePaneNode getNode() {
		return node;
	}
}
