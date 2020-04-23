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

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.sidebar.event.SidebarChangeNodeEvent;
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

	private SidePane sidePane;

	private boolean top;

	private String name;
	private Label label;

	private double relativeDragPosition;

	/**
	 * Creates the header.
	 *
	 * @param sidePane the {@link SidePane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param name     the name of the {@link javafx.scene.Node}.
	 * @param top      whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 */
	public SidePaneNodeHeader(SidePane sidePane, String name, boolean top, String languageNode) {
		this.sidePane = sidePane;
		this.name = name;

		this.top = top;

		getStyleClass().add("side-pane-node-header");
		setPrefHeight(HEIGHT);

		if (!top) {
			setCursor(sidePane.getTop() == null ? TOP_NULL_CURSOR : TOP_NOT_NULL_CURSOR);
		}

		label = languageNode == null ? new Label(name) : new LanguageLabel(languageNode);
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);

		sidePane.registerListeners(this);
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
