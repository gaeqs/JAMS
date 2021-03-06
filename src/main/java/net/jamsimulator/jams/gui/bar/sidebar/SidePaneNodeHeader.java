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

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.sidebar.event.SidebarChangeNodeEvent;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents the header of a {@link SidePaneNode}. This header contains
 * information and options about the wrapped {@link javafx.scene.Node} of the {@link SidePaneNode}.
 * <p>
 * This header also allows to resize the {@link javafx.scene.Node}.
 *
 * @see SidePaneNode
 */
public class SidePaneNodeHeader extends AnchorPane {

	public static final int HEIGHT = 25;
	public static final Cursor TOP_NULL_CURSOR = Cursor.DEFAULT;
	public static final Cursor TOP_NOT_NULL_CURSOR = Cursor.N_RESIZE;

	private final SidePane sidePane;

	private final boolean top;

	private final String name;
	private final Label label;
	private final Button closeButton;

	private double relativeDragPosition;

	/**
	 * Creates the header.
	 *
	 * @param sidePane the {@link SidePane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param top      whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 * @param node     the node holding this header.
	 * @param sidebar  the sidebar.
	 */
	public SidePaneNodeHeader(SidePane sidePane, boolean top, SidePaneNode node, Sidebar sidebar) {
		this.sidePane = sidePane;
		this.name = node.getSnapshot().getName();

		this.top = top;

		getStyleClass().add("side-pane-node-header");
		setPrefHeight(HEIGHT);

		if (!top) {
			setCursor(sidePane.getTop() == null ? TOP_NULL_CURSOR : TOP_NOT_NULL_CURSOR);
		}

		label = node.getSnapshot().getLanguageNode() == null ? new Label(name) : new LanguageLabel(node.getSnapshot().getLanguageNode());
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);

		Image closeImage = JamsApplication.getIconManager().getOrLoadSafe(Icons.BAR_CLOSE).orElse(null);
		closeButton = new Button("", new NearestImageView(closeImage, 16, 16));
		closeButton.getStyleClass().add("side-pane-node-header-button");
		closeButton.setOnAction(event -> sidebar.get(name).ifPresent(SidebarButton::hide));
		closeButton.setCursor(Cursor.HAND);
		AnchorUtils.setAnchor(closeButton, 0, 0, -1, 1);
		getChildren().add(closeButton);

		sidePane.registerListeners(this, true);
		registerFXEvents();
	}

	/**
	 * Returns the name of the header.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link Label} that has the name of the header.
	 *
	 * @return the {@link Label}.
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Returns whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 *
	 * @return whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 */
	public boolean isTop() {
		return top;
	}

	@Listener
	private void onTopChange(SidebarChangeNodeEvent event) {
		if (top || !event.isTop()) return;

		if (event.getFrom() == null && event.getTo() != null) {
			setCursor(TOP_NOT_NULL_CURSOR);
		} else if (event.getFrom() != null && event.getTo() == null) {
			setCursor(TOP_NULL_CURSOR);
		}
	}

	private void registerFXEvents() {
		if (top) return;
		setOnMousePressed(event -> relativeDragPosition = event.getY());
		setOnMouseDragged(event -> {
			if (getCursor() == TOP_NULL_CURSOR) return;
			double absolute = event.getSceneY();
			double min = sidePane.getLocalToSceneTransform().getTy();
			double max = min + sidePane.getHeight();
			double relative = (absolute - min - relativeDragPosition) / (max - min);
			sidePane.setDividerPosition(0, relative);
		});
	}
}
