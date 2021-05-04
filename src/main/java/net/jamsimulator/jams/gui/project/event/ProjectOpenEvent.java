package net.jamsimulator.jams.gui.project.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.project.Project;

/**
 * This event is called when a project is opened.
 */
public class ProjectOpenEvent extends ProjectEvent {

    ProjectOpenEvent(Project project) {
        super(project);
    }

    public static class Before extends ProjectOpenEvent implements Cancellable {

        private boolean cancelled;

        public Before(Project project) {
            super(project);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After extends ProjectOpenEvent {

        private final ProjectTab projectTab;

        public After(Project project, ProjectTab projectTab) {
            super(project);
            this.projectTab = projectTab;
        }

        public ProjectTab getProjectTab() {
            return projectTab;
        }
    }
}
