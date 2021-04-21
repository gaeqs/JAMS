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

package net.jamsimulator.jams.gui.start;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.window.WindowButtonClose;
import net.jamsimulator.jams.gui.main.window.WindowButtonMinimize;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;


public class StartWindowTopBar extends AnchorPane {


	private final ImageView view;
	private final Label title;
	private final HBox viewTitleBox;
	private final HBox windowButtons;

	public StartWindowTopBar(Stage stage) {
		getStyleClass().add("start-top-bar");
		view = new ImageView(JamsApplication.getIconManager()
				.getOrLoadSafe(Icons.LOGO).orElse(null));
		view.setFitWidth(20);
		view.setFitHeight(20);
		title = new LanguageLabel(Messages.START_TITLE, "{VERSION}", Jams.getVersion());
		title.getStyleClass().add("start-title");

		viewTitleBox = new HBox(view, title);
		viewTitleBox.setSpacing(5);
		viewTitleBox.setAlignment(Pos.CENTER_LEFT);
		AnchorUtils.setAnchor(viewTitleBox, 5, 5, 5, -1);

		windowButtons = new HBox();
		generateButtons(stage);
		AnchorUtils.setAnchor(windowButtons, 0, 0, -1, 0);


		refresh();
	}

	public ImageView getView() {
		return view;
	}

	public Label getTitle() {
		return title;
	}

	public HBox getWindowButtons() {
		return windowButtons;
	}

	private void generateButtons(Stage stage) {
		windowButtons.setSpacing(0);

		Button minimize = new WindowButtonMinimize(stage);
		Button close = new WindowButtonClose(stage);
		minimize.getStyleClass().add("start-top-bar-window_button-minimize");
		close.getStyleClass().add("start-top-bar-window_button-close");

		minimize.setPrefWidth(30);
		minimize.setPrefHeight(30);
		close.setPrefWidth(30);
		close.setPrefHeight(30);

		windowButtons.getChildren().addAll(minimize, close);

		Platform.runLater(() -> {
			close.setBorder(Border.EMPTY);
			minimize.setBorder(Border.EMPTY);
		});
	}

	private void refresh() {
		getChildren().clear();
		getChildren().addAll(viewTitleBox, windowButtons);
	}
}
