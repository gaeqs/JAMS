package net.jamsimulator.jams.gui.project;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.main.MainAnchorPane;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a folder project's tab. This must be used by {@link MainAnchorPane#getProjectsTabPane()}
 */
public class ProjectTab extends Tab {

	private Project project;
	private TabPane projectTabPane;

	private List<EventHandler<Event>> closeListeners;

	/**
	 * Creates the folder project's tab.
	 *
	 * @param project the handled project.
	 */
	public ProjectTab(Project project) {
		super(project.getName());
		this.project = project;
		closeListeners = new ArrayList<>();

		AnchorPane pane = new AnchorPane();

		//Black line separator
		Separator separator = new Separator(Orientation.HORIZONTAL);
		AnchorUtils.setAnchor(separator, 0, -1, 0, 0);
		pane.getChildren().add(separator);

		projectTabPane = new TabPane();
		AnchorUtils.setAnchor(projectTabPane, 1, 0, 0, 0);
		pane.getChildren().add(projectTabPane);

		Tab tab = new Tab("Project Structure");
		tab.setClosable(false);
		projectTabPane.getTabs().add(tab);

		WorkingPane structurePane = new ProjectPane(tab, this, project);
		tab.setContent(structurePane);

		setContent(pane);

		setOnClosed(event -> closeListeners.forEach(target -> target.handle(event)));
	}

	/**
	 * Returns the project handled by this tab-
	 *
	 * @return the project.
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Returns the {@link TabPane} of this project tab.
	 *
	 * @return the {@link TabPane}.
	 */
	public TabPane getProjectTabPane() {
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
