package net.jamsimulator.jams.gui.main;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.project.ProjectsTabPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * This is the main pane of JAMS's main window.
 * It contains the top {@link MenuBar} and the {@link ProjectsTabPane}.
 */
public class MainAnchorPane extends AnchorPane {

	private MenuBar topMenuBar;
	private ProjectsTabPane projectsTabPane;

	/**
	 * Creates the main anchor pane.
	 */
	public MainAnchorPane() {
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
	 * Returns the {@link ProjectsTabPane}.
	 *
	 * @return the {@link ProjectsTabPane}.
	 */
	public ProjectsTabPane getProjectsTabPane() {
		return projectsTabPane;
	}

	private void generateTopMenuBar() {
		topMenuBar = new MainMenuBar();
		getChildren().add(topMenuBar);
		AnchorUtils.setAnchor(topMenuBar, -1, -1, 0, 0);
	}

	private void generateProjectsTabPane() {
		projectsTabPane = new ProjectsTabPane();
		getChildren().add(projectsTabPane);
		AnchorUtils.setAnchor(projectsTabPane, 23, 0, 0, 0);
	}
}