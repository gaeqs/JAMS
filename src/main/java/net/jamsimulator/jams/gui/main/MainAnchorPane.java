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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.window.WindowButton;
import net.jamsimulator.jams.gui.main.window.WindowButtonClose;
import net.jamsimulator.jams.gui.main.window.WindowButtonMaximize;
import net.jamsimulator.jams.gui.main.window.WindowButtonMinimize;
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * This is the main pane of JAMS's main window.
 * It contains the top {@link MenuBar} and the {@link ProjectListTabPane}.
 */
public class MainAnchorPane extends AnchorPane {

	private MenuBar topMenuBar;
	private HBox windowButtons;
	private ProjectListTabPane projectListTabPane;

	/**
	 * Creates the main anchor pane.
	 */
	public MainAnchorPane() {
		getStyleClass().add("anchor-pane");
		generateTopMenuBar();
		generateWindowButtons();
		generateProjectsTabPane();
	}

	/**
	 * Returns the top {@link MenuBar}.
	 *
	 * @return the {@link MenuBar}.
	 */
	public MenuBar getTopMenuBar() {
		return topMenuBar;
	}

	/**
	 * Returns the {@link ProjectListTabPane}.
	 *
	 * @return the {@link ProjectListTabPane}.
	 */
	public ProjectListTabPane getProjectListTabPane() {
		return projectListTabPane;
	}

	private void generateTopMenuBar() {
		topMenuBar = new MainMenuBar();
		getChildren().add(topMenuBar);
		AnchorUtils.setAnchor(topMenuBar, -1, -1, 0, 100);
	}

	private void generateWindowButtons() {
		windowButtons = new HBox();
		windowButtons.setSpacing(0);

		getChildren().add(windowButtons);
		AnchorUtils.setAnchor(windowButtons, -1, -1, -1, 0);
		windowButtons.prefHeightProperty().bind(topMenuBar.heightProperty());

		Button close = new WindowButtonClose(JamsApplication.getStage());
		Button maximize = new WindowButtonMaximize(JamsApplication.getStage());
		Button minimize = new WindowButtonMinimize(JamsApplication.getStage());

		close.setAlignment(Pos.CENTER);
		maximize.setAlignment(Pos.CENTER);
		minimize.setAlignment(Pos.CENTER);

		windowButtons.getChildren().addAll(minimize, maximize, close);

		Platform.runLater(() -> {
			close.setBorder(Border.EMPTY);
			maximize.setBorder(Border.EMPTY);
			minimize.setBorder(Border.EMPTY);
		});
	}

	private void generateProjectsTabPane() {
		projectListTabPane = new ProjectListTabPane();
		getChildren().add(projectListTabPane);
		AnchorUtils.setAnchor(projectListTabPane, 23, 0, 0, 0);
	}
}