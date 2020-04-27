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

package net.jamsimulator.jams.gui.project;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a folder project's tab. This must be used by {@link MainAnchorPane#getProjectListTabPane()}
 */
public class ProjectTab extends Tab {

	private final MipsProject project;
	private final ProjectTabPane projectTabPane;

	private final List<EventHandler<Event>> closeListeners;

	/**
	 * Creates the folder project's tab.
	 *
	 * @param project the handled project.
	 */
	public ProjectTab(MipsProject project) {
		super(project.getName());
		setClosable(true);
		this.project = project;
		closeListeners = new ArrayList<>();

		AnchorPane pane = new AnchorPane();

		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		pane.getChildren().add(separator);

		projectTabPane = new ProjectTabPane(this);
		AnchorUtils.setAnchor(projectTabPane, 1, 0, 0, 0);
		pane.getChildren().add(projectTabPane);
		setContent(pane);

		setOnClosed(event -> {
			closeListeners.forEach(target -> target.handle(event));
			project.assignProjectTab(null);
		});
	}

	/**
	 * Returns the project handled by this tab-
	 *
	 * @return the project.
	 */
	public MipsProject getProject() {
		return project;
	}

	/**
	 * Returns the {@link TabPane} of this project tab.
	 *
	 * @return the {@link TabPane}.
	 */
	public ProjectTabPane getProjectTabPane() {
		return projectTabPane;
	}

	/**
	 * Adds a listener that will be invoked when the tab is closed.
	 *
	 * @param listener the listener.
	 */
	public void addTabCloseListener(EventHandler<Event> listener) {
		closeListeners.add(listener);
	}


	/**
	 * Removed a listener that would be invoked when the tab is closed.
	 *
	 * @param listener the listener.
	 */
	public void removeStageCloseListener(EventHandler<Event> listener) {
		closeListeners.remove(listener);
	}
}
