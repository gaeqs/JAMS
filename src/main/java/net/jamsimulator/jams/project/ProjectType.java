package net.jamsimulator.jams.project;

import net.jamsimulator.jams.manager.Labeled;

import java.io.File;

/**
 * Represents the type of a {@link Project}. This type allows projects to be managed by
 * their respective {@link Project} class.
 *
 * @param <T> the project to load or create.
 */
public abstract class ProjectType<T extends Project> implements Labeled {

    private final String name;

    /**
     * Creates the project type.
     *
     * @param name the name of the project type.
     */
    public ProjectType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Creates a project. This method does the same operations as {@link #loadProject(File)},
     * but setting the name forcefully instead of getting it from the data file if exists.
     * <p>
     * All missing files will be created with default data. This create method may also
     * create several default data that {@link #loadProject(File)} won't create, such as
     * the default {@link net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration
     * simulation configuration}
     * in a {@link net.jamsimulator.jams.project.mips.MIPSProject MIPS project}.
     *
     * @param name   the name of the project.
     * @param folder the folder taht will contain the folder. This folder must already exist!
     * @return the new project.
     */
    public abstract T createProject(String name, File folder);

    /**
     * Loads a project. This method loads all the data from the given folder and threat it as a project
     * of the represented type.
     * <p>
     * All missing files will be created with default data.
     *
     * @param folder the folder taht will contain the folder. This folder must already exist!
     * @return the new project.
     */
    public abstract T loadProject(File folder);
}
