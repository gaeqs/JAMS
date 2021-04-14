package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.event.ProjectTypeRegisterEvent;
import net.jamsimulator.jams.project.event.ProjectTypeUnregisterEvent;
import net.jamsimulator.jams.project.mips.MIPSProjectType;

/**
 * This singleton stores all {@link ProjectType}s that projects may use.
 * <p>
 * To register an {@link ProjectType} use {@link #add(Object)}.
 * To unregister an {@link ProjectType} use {@link #remove(Object)}.
 * An {@link ProjectType}'s removal from the manager doesn't make editors to stop using
 * it inmediatelly.
 */
public class ProjectTypeManager extends Manager<ProjectType<?>> {

    public static final ProjectTypeManager INSTANCE = new ProjectTypeManager();

    /**
     * Creates the manager.
     */
    public ProjectTypeManager() {
        super(ProjectTypeRegisterEvent.Before::new, ProjectTypeRegisterEvent.After::new,
                ProjectTypeUnregisterEvent.Before::new, ProjectTypeUnregisterEvent.After::new);
    }

    @Override
    protected void loadDefaultElements() {
        add(MIPSProjectType.INSTANCE);
    }
}
