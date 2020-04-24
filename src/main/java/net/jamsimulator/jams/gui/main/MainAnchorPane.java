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
import net.jamsimulator.jams.gui.project.ProjectListTabPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * This is the main pane of JAMS's main window.
 * It contains the top {@link MenuBar} and the {@link ProjectListTabPane}.
 */
public class MainAnchorPane extends AnchorPane {

	private MenuBar topMenuBar;
	private ProjectListTabPane projectListTabPane;

	/**
	 * Creates the main anchor pane.
	 */
	public MainAnchorPane() {
		getStyleClass().add("anchor-pane");
		generateTopMenuBar();
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
		AnchorUtils.setAnchor(topMenuBar, -1, -1, 0, 0);
	}

	private void generateProjectsTabPane() {
		projectListTabPane = new ProjectListTabPane();
		getChildren().add(projectListTabPane);
		AnchorUtils.setAnchor(projectListTabPane, 23, 0, 0, 0);
	}
}