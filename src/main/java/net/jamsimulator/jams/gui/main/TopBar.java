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

package net.jamsimulator.jams.gui.main;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.window.WindowButtonClose;
import net.jamsimulator.jams.gui.main.window.WindowButtonMaximize;
import net.jamsimulator.jams.gui.main.window.WindowButtonMinimize;
import net.jamsimulator.jams.utils.AnchorUtils;


public class TopBar extends AnchorPane {


	private final ImageView view;
	private final MainMenuBar menuBar;
	private final HBox windowButtons;

	private boolean transparentMode;

	public TopBar(Stage stage, boolean transparentMode) {
		getStyleClass().add("top-bar");
		this.transparentMode = transparentMode;
		view = new ImageView(JamsApplication.getIconManager()
				.getOrLoadSafe(Icons.LOGO).orElse(null));
		view.setFitWidth(20);
		view.setFitHeight(20);
		AnchorUtils.setAnchor(view, 5, 5, 5, -1);

		menuBar = new MainMenuBar();

		windowButtons = new HBox();
		generateButtons(stage);
		AnchorUtils.setAnchor(windowButtons, 0, 0, -1, 0);


		refresh();
	}

	public ImageView getView() {
		return view;
	}

	public MainMenuBar getMenuBar() {
		return menuBar;
	}

	public HBox getWindowButtons() {
		return windowButtons;
	}

	private void generateButtons(Stage stage) {
		windowButtons.setSpacing(0);

		Button minimize = new WindowButtonMinimize(stage);
		Button maximize = new WindowButtonMaximize(stage);
		Button close = new WindowButtonClose(stage);

		minimize.setPrefWidth(30);
		minimize.setPrefHeight(30);
		maximize.setPrefWidth(30);
		maximize.setPrefHeight(30);
		close.setPrefWidth(30);
		close.setPrefHeight(30);

		windowButtons.getChildren().addAll(minimize, maximize, close);

		Platform.runLater(() -> {
			close.setBorder(Border.EMPTY);
			maximize.setBorder(Border.EMPTY);
			minimize.setBorder(Border.EMPTY);
		});
	}

	public boolean isTransparentMode() {
		return transparentMode;
	}

	public void setTransparentMode(boolean transparentMode) {
		if (transparentMode == this.transparentMode) return;
		this.transparentMode = transparentMode;
		refresh();
	}

	private void refresh() {
		getChildren().clear();
		AnchorUtils.setAnchor(menuBar, 0, 0, transparentMode ? 25 : 0, transparentMode ? 100 : 0);

		if (transparentMode) {
			getChildren().addAll(view, menuBar, windowButtons);
		} else {
			getChildren().add(menuBar);
		}
	}
}
