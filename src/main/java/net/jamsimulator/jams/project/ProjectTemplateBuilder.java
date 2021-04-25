package net.jamsimulator.jams.project;

import javafx.scene.image.Image;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

public abstract class ProjectTemplateBuilder<T extends Project> {

    private final String name;
    private final String languageNode;
    private final Image icon;

    public ProjectTemplateBuilder(String name, String languageNode, Image icon) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.languageNode = languageNode;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getLanguageNode() {
        return Optional.ofNullable(languageNode);
    }

    public Optional<Image> getIcon() {
        return Optional.ofNullable(icon);
    }

    public abstract ProjectTemplate<T> createBuilder();
}
