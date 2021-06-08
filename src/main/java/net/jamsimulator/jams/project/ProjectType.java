package net.jamsimulator.jams.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents the type of a {@link Project}. This type allows projects to be managed by
 * their respective {@link Project} class.
 *
 * @param <T> the project to load or create.
 */
public abstract class ProjectType<T extends Project> implements Labeled {

    protected final String name;
    protected final Image icon;
    protected final ObservableList<ProjectTemplateBuilder<?>> templateBuilders;

    /**
     * Creates the project type.
     *
     * @param name the name of the project type.
     * @param icon the icon representing this type. It may be null.
     */
    public ProjectType(String name, Image icon) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.icon = icon;
        this.templateBuilders = FXCollections.observableList(new ArrayList<>());
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Image icon} representing this type, if present.
     *
     * @return the {@link Image icon}.
     */
    public Optional<Image> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * Returns a modifiable list with all {@link ProjectTemplateBuilder} registered in this project type.
     * <p>
     * {@link ProjectTemplateBuilder}s are used to create {@link Project}s with custom parameters.
     * These builders also provide graphical nodes for the project creation window.
     * <p>
     * This list is observable. You can listen modifications of this list.
     *
     * @return the {@link ObservableList}.
     */
    public ObservableList<ProjectTemplateBuilder<?>> getTemplateBuilders() {
        return templateBuilders;
    }

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
