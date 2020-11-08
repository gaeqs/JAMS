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

package net.jamsimulator.jams.gui.main.window;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.image.NearestImageView;

public class WindowButton extends Button {

	public static final double FIT = 12;
	public static final String STYLE_CLASS = "window-button";

	protected final Stage stage;
	protected final ImageView imageView;

	public WindowButton(Stage stage, Image display) {
		super("", new NearestImageView(display, FIT, FIT));
		getStyleClass().add(STYLE_CLASS);
		setAlignment(Pos.CENTER);
		this.stage = stage;
		this.imageView = (ImageView) getGraphic();

		imageView.imageProperty().addListener((obs, old, val) -> {
			Platform.runLater(() -> {
				imageView.setFitWidth(FIT);
				imageView.setFitHeight(FIT);
			});
		});
	}

	public Stage getStage() {
		return stage;
	}
}
