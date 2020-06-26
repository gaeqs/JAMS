package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanPropertyEditor extends CheckBox implements PropertyEditor<Boolean> {

	private final Property<Boolean> property;

	public BooleanPropertyEditor(Property<Boolean> property) {
		this.property = property;
		setSelected(property.getValue());
		property.bind(selectedProperty());
	}

	@Override
	public Property<Boolean> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}
}
