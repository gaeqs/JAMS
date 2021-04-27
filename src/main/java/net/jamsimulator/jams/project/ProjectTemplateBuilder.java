package net.jamsimulator.jams.project;

import javafx.scene.image.Image;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * This class creates {@link ProjectTemplate}s of a certain type.
 * <p>
 * This class also contains the common data for these templates, such the name, the language node or the icon.
 *
 * @param <T> the project the {@link ProjectTemplate}s create.
 */
public abstract class ProjectTemplateBuilder<T extends Project> {

    protected final String name;
    protected final String languageNode;
    protected final Image icon;

    /**
     * Creates the new template builder.
     *
     * @param name         the name of the template.
     * @param languageNode the language node or null.
     * @param icon         the icon or null.
     */
    public ProjectTemplateBuilder(String name, String languageNode, Image icon) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.languageNode = languageNode;
        this.icon = icon;
    }

    /**
     * Returns the name representing the {@link ProjectTemplate}s.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the language node representing the {@link ProjectTemplate}s if present.
     *
     * @return the language node if present.
     */
    public Optional<String> getLanguageNode() {
        return Optional.ofNullable(languageNode);
    }

    /**
     * Returns the {@link Image icon} representing the {@link ProjectTemplate}s if present.
     *
     * @return the {@link Image icon} if present.
     */
    public Optional<Image> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * Creates a {@link ProjectTemplate} of the represented type of this builder.
     *
     * @return the {@link ProjectTemplate}.
     */
    public abstract ProjectTemplate<T> createBuilder();
}
