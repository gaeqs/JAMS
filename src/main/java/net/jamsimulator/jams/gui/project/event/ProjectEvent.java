package net.jamsimulator.jams.gui.project.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.project.Project;

/**
 * Base class for event related to project-management in the JavaFX app.
 */
public class ProjectEvent extends Event {

    private final Project project;

    public ProjectEvent (Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
