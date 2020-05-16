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

import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;

public class WindowButtonMaximize extends WindowButton {

	private final Image maximized, windowed;

	public WindowButtonMaximize(Stage stage) {
		super(stage, null);
		this.maximized = JamsApplication.getIconManager().getOrLoadSafe(Icons.WINDOW_UNMAXIMIZE, Icons.WINDOW_UNMAXIMIZE_PATH, 12, 12).orElse(null);
		this.windowed = JamsApplication.getIconManager().getOrLoadSafe(Icons.WINDOW_MAXIMIZE, Icons.WINDOW_MAXIMIZE_PATH, 12, 12).orElse(null);

		imageView.setImage(stage.isMaximized() ? maximized : windowed);

		setOnAction(event -> onAction());
		stage.maximizedProperty().addListener(this::onMaximizedChange);

		if(stage.isMaximized()) {
			getStyleClass().add("window_button-unmaximize");
		}
		else {
			getStyleClass().add("window_button-maximize");
		}
	}

	private void onAction() {
		stage.setMaximized(!stage.isMaximized());
	}

	private void onMaximizedChange(Object obs, boolean old, boolean val) {
		imageView.setImage(val ? maximized : windowed);

		if(stage.isMaximized()) {
			getStyleClass().remove("window_button-maximize");
			getStyleClass().add("window_button-unmaximize");
		}
		else {
			getStyleClass().remove("window_button-unmaximize");
			getStyleClass().add("window_button-maximize");
		}
	}
}
