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

import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;

/**
 * This is the main pane of JAMS's main window.
 * It contains the top {@link MenuBar} and the {@link ProjectListTabPane}.
 */
public class MainAnchorPane extends AnchorPane {

	private TopBar topBar;
	private ProjectListTabPane projectListTabPane;

	/**
	 * Creates the main anchor pane.
	 */
	public MainAnchorPane(Stage stage, boolean transparent) {
		getStyleClass().add("anchor-pane");
		getStyleClass().add("main-anchor-pane");
		generateTopMenuBar(stage, transparent);
		generateProjectsTabPane();
	}

	public TopBar getTopBar() {
		return topBar;
	}

	/**
	 * Returns the {@link ProjectListTabPane}.
	 *
	 * @return the {@link ProjectListTabPane}.
	 */
	public ProjectListTabPane getProjectListTabPane() {
		return projectListTabPane;
	}

	private void generateTopMenuBar(Stage stage, boolean transparent) {
		topBar = new TopBar(stage, transparent);
		getChildren().add(topBar);
		AnchorUtils.setAnchor(topBar, -1, -1, 0, 0);
		topBar.setPrefHeight(30);
	}

	private void generateProjectsTabPane() {
		projectListTabPane = new ProjectListTabPane();
		getChildren().add(projectListTabPane);
		AnchorUtils.setAnchor(projectListTabPane, 30, 0, 0, 0);
	}
}