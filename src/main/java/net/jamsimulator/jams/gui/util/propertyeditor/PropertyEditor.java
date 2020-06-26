package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;

public interface PropertyEditor<T> {

	Property<T> getProperty();

	Node thisInstanceAsNode();
}
