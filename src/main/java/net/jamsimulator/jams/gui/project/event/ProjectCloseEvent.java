package net.jamsimulator.jams.gui.project.event;

import net.jamsimulator.jams.project.Project;

/**
 * This event is called when a project is closed.
 */
public class ProjectCloseEvent extends ProjectEvent {

    public ProjectCloseEvent(Project project) {
        super(project);
    }
}
