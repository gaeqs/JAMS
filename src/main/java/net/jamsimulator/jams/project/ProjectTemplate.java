package net.jamsimulator.jams.project;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;

public abstract class ProjectTemplate<E extends Project> {

    protected final ProjectType<E> type;

    public ProjectTemplate(ProjectType<E> type) {
        this.type = type;
    }

    public ProjectType<E> getType() {
        return type;
    }

    public abstract Node getBuilderNode();

    public abstract BooleanProperty validProperty();

    public abstract E build();

}
