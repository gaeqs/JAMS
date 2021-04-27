package net.jamsimulator.jams.project;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import net.jamsimulator.jams.project.exception.MIPSTemplateBuildException;

/**
 * Represents the template of a {@link Project}. A project template is used to create new projects
 * modifying this template params.
 * <p>
 * This template also manages the {@link Node JavaFX representation}. This alows users to create
 * new projects easily.
 *
 * @param <E> the project to create.
 */
public abstract class ProjectTemplate<E extends Project> {

    protected final ProjectType<E> type;

    /**
     * Creates the project template.
     *
     * @param type the {@link ProjectType} representing the @link Project} to create.
     */
    public ProjectTemplate(ProjectType<E> type) {
        this.type = type;
    }

    /**
     * Returns the {@link ProjectType} of the {@link Project} to create.
     *
     * @return the {@link ProjectType}.
     */
    public ProjectType<E> getType() {
        return type;
    }

    /**
     * Returns the {@link Node JavaFX representation} of this template. This allows users
     * to modify this template easily.
     *
     * @return the {@link Node JavaFX representation}.
     */
    public abstract Node getBuilderNode();

    /**
     * Returns a property representing whether this template has valid data and can create a new project.
     *
     * @return whether this template can create a new project with its current data.
     */
    public abstract BooleanProperty validProperty();

    /**
     * Creates a new {@link Project} with the data of this template.
     * <p>
     * If any error occurs during creation, this method throws a {@link MIPSTemplateBuildException}.
     *
     * @return the new {@link Project}.
     * @throws MIPSTemplateBuildException when any error occurs during creation.
     */
    public abstract E build() throws MIPSTemplateBuildException;

}
