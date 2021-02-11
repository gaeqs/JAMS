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

package net.jamsimulator.jams.gui.popup;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.util.AnchorUtils;

class PopupWindowHelper {

	static void open(Stage stage, Node node, int width, int height, boolean transparent) {
		if (transparent) {
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.focusedProperty().addListener((obs, old, val) -> {
				if (!val) stage.close();
			});
		} else {
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setResizable(false);
		}

		stage.initOwner(JamsApplication.getStage());

		AnchorPane background = new AnchorPane();
		background.getStyleClass().add("window-popup-background");
		AnchorUtils.setAnchor(node, 0, 0, 0, 0);

		if (width >= 0) {
			background.setPrefWidth(width);
		}
		if (height >= 0) {
			background.setPrefHeight(height);
		}

		background.getChildren().add(node);
		ThemedScene scene = new ThemedScene(background);

		stage.setScene(scene);

		if (node instanceof Region) {
			background.applyCss();
			background.layout();
		}


		stage.show();
		Platform.runLater(() -> {
			Stage main = JamsApplication.getStage();
			stage.setX(main.getX() + main.getWidth() / 2 - background.getWidth() / 2);
			stage.setY(main.getY() + main.getHeight() / 2 - background.getHeight() / 2);
		});

	}

}
