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
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.bar.sidebar.SidebarButton;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * Represents a button inside a {@link BottomBar}.
 */
public class BottomBarButton extends ToggleButton implements BarButton {

	public static final int IMAGE_SIZE = 16;

	private final BottomBar bottomBar;
	private final BottomPaneNode pane;

	/**
	 * Creates a bottom bar button.
	 *
	 * @param bottomBar the bottom bar handling this button.
	 * @param pane      the {@link BottomPaneNode}.
	 */
	public BottomBarButton(BottomBar bottomBar, BottomPaneNode pane) {
		this.bottomBar = bottomBar;
		this.pane = pane;

		getStyleClass().addAll("bottom-bar-button");

		Label label = pane.getSnapshot().getLanguageNode() == null
				? new Label(pane.getSnapshot().getName())
				: new LanguageLabel(pane.getSnapshot().getLanguageNode());
		Group group = new Group(label);

		if (pane.getSnapshot().getIcon() != null) {
			ImageView imageView = new NearestImageView(pane.getSnapshot().getIcon(), IMAGE_SIZE, IMAGE_SIZE);
			HBox hBox = new HBox(imageView, group);
			hBox.setSpacing(4);
			hBox.setAlignment(Pos.CENTER);
			setGraphic(hBox);
		} else {
			setGraphic(group);
		}
		setPrefHeight(WorkingPane.SIDEBAR_WIDTH);

		loadSelectedListener();
		loadDragAndDropListeners();
	}

	@Override
	public BottomBar getProjectBar() {
		return bottomBar;
	}

	@Override
	public String getName() {
		return pane.getSnapshot().getName();
	}

	@Override
	public PaneSnapshot getSnapshot() {
		return pane.getSnapshot();
	}

	@Override
	public BottomPaneNode getPane() {
		return pane;
	}

	private void loadSelectedListener() {
		selectedProperty().addListener((obs, old, val) -> {
			if (old == val) return;
			if (val) {
				bottomBar.select(this);
			} else if (bottomBar.getCurrent().orElse(null) == pane) {
				bottomBar.select(null);
			}
		});
	}

	private void loadDragAndDropListeners() {
		addEventHandler(DragEvent.DRAG_OVER, event -> {
			if (!event.getDragboard().hasString() ||
					!event.getDragboard().getString().startsWith(SidebarButton.DRAG_DROP_PREFIX))
				return;
			event.acceptTransferModes(TransferMode.COPY);
			event.consume();
		});

		addEventHandler(DragEvent.DRAG_DROPPED, event -> {
			String name = event.getDragboard().getString().substring(SidebarButton.DRAG_DROP_PREFIX.length() + 1);
			bottomBar.manageDrop(name, bottomBar.getChildren().indexOf(this));
			event.setDropCompleted(true);
			event.consume();
		});

		addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
			Dragboard dragboard = startDragAndDrop(TransferMode.COPY);

			ClipboardContent content = new ClipboardContent();
			content.putString(SidebarButton.DRAG_DROP_PREFIX + ":" + getName());
			dragboard.setContent(content);

			event.consume();
		});
	}
}
