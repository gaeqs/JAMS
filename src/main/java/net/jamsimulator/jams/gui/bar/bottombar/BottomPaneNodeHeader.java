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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;
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

	private final SplitPane verticalSplitPane;

	private final String name;
	private final Label label;
	private final Button closeButton;

	private double relativeDragPosition;


	/**
	 * Creates the header.
	 *
	 * @param verticalSplitPane the {@link SplitPane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param snapshot          the snapshot.
	 * @param bottomBar         the {@link BottomBar} holding this pane.
	 */
	public BottomPaneNodeHeader(SplitPane verticalSplitPane, PaneSnapshot snapshot, BottomBar bottomBar) {
		this.verticalSplitPane = verticalSplitPane;
		this.name = snapshot.getName();

		getStyleClass().add("bottom-pane-node-header");
		setPrefHeight(HEIGHT);

		setCursor(CURSOR);

		label = snapshot.getLanguageNode() == null ? new Label(name) : new LanguageLabel(snapshot.getLanguageNode());
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);

		Image closeImage = JamsApplication.getIconManager().getOrLoadSafe(Icons.BAR_CLOSE, Icons.BAR_CLOSE_PATH, 1024, 1024).orElse(null);
		closeButton = new Button("", new NearestImageView(closeImage, 16, 16));
		closeButton.getStyleClass().add("side-pane-node-header-button");
		closeButton.setOnAction(event -> bottomBar.get(name).ifPresent(BottomBarButton::hide));
		closeButton.setCursor(Cursor.HAND);
		AnchorUtils.setAnchor(closeButton, 0, 0, -1, 1);
		getChildren().add(closeButton);

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
