package net.jamsimulator.jams.gui.util.propertyeditor;

import com.sun.javafx.scene.control.IntegerField;
import javafx.beans.property.Property;
import javafx.scene.Node;

public class IntegerPropertyEditor extends IntegerField implements PropertyEditor<Integer> {

	private final Property<Integer> property;

	public IntegerPropertyEditor(Property<Integer> property) {
		this.property = property;

		setValue(property.getValue());
		setOnAction(event -> property.setValue(getValue()));
		focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			property.setValue(getValue());
		});

	}

	@Override
	public Property<Integer> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}
}
