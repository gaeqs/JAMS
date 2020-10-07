package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;

import java.util.function.Consumer;

public interface PropertyEditor<T> {

	Property<?> getProperty();

	Node thisInstanceAsNode();

	void addListener(Consumer<T> consumer);
}
