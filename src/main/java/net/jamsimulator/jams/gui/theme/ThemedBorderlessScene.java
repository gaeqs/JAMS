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

package net.jamsimulator.jams.gui.theme;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.event.CodeFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.GeneralFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;

public class ThemedBorderlessScene extends BorderlessScene {

	public ThemedBorderlessScene(Stage stage, Parent root) {
		super(stage, StageStyle.TRANSPARENT, root);
		initializeJamsListeners();
		setTransparentWindowStyle("-fx-background-color:rgb(0,0,0,0.2); -fx-border-color:transparent;");
		setSnapEnabled(System.getProperty("os.name").toLowerCase().contains("win"));
		removeDefaultCSS();
	}

	public ThemedBorderlessScene(Stage stage, Parent root, double width, double height) {
		super(stage, StageStyle.TRANSPARENT, root, width, height);
		initializeJamsListeners();
		setTransparentWindowStyle("-fx-background-color:rgb(0,0,0,0.2); -fx-border-color:transparent;");
		setSnapEnabled(System.getProperty("os.name").toLowerCase().contains("win"));
		removeDefaultCSS();
	}

	protected void initializeJamsListeners() {
		JamsApplication.getThemeManager().registerListeners(this, true);
		JamsApplication.getThemeManager().getSelected().apply(this);
	}

	@Listener
	public void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}

	@Listener
	public void onThemeChange(GeneralFontChangeEvent.After event) {
		JamsApplication.getThemeManager().getSelected().apply(this);
	}

	@Listener
	public void onThemeChange(CodeFontChangeEvent.After event) {
		JamsApplication.getThemeManager().getSelected().apply(this);
	}

}
