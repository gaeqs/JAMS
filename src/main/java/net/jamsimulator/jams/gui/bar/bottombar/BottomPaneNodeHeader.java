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

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents the header of a {@link BottomPaneNode}. This header contains
 * information and options about the wrapped {@link javafx.scene.Node} of the {@link BottomPaneNode}.
 * <p>
 * This header also allows to resize the {@link javafx.scene.Node}.
 *
 * @see BottomPaneNode
 */
public class BottomPaneNodeHeader extends AnchorPane {

	public static final int HEIGHT = 25;
	public static final Cursor CURSOR = Cursor.N_RESIZE;

	private SplitPane verticalSplitPane;

	private String name;
	private Label label;

	private double relativeDragPosition;


	/**
	 * Creates the header.
	 *
	 * @param verticalSplitPane the {@link SplitPane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param name              the name of the {@link javafx.scene.Node}.
	 * @param languageNode      the language node to display or null.
	 */
	public BottomPaneNodeHeader(SplitPane verticalSplitPane, String name, String languageNode) {
		this.verticalSplitPane = verticalSplitPane;
		this.name = name;

		getStyleClass().add("bottom-pane-node-header");
		setPrefHeight(HEIGHT);

		setCursor(CURSOR);

		label = languageNode == null ? new Label(name) : new LanguageLabel(languageNode);
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);
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

	private void registerFXEvents() {
		setOnMousePressed(event -> relativeDragPosition = event.getY());
		setOnMouseDragged(event -> {
			double absolute = event.getSceneY();
			double min = verticalSplitPane.getLocalToSceneTransform().getTy();
			double max = min + verticalSplitPane.getHeight();
			double relative = (absolute - min - relativeDragPosition) / (max - min);
			verticalSplitPane.setDividerPosition(0, relative);
		});
	}
}
