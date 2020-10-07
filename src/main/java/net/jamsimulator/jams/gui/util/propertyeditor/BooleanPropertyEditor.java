package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

public class BooleanPropertyEditor extends CheckBox implements PropertyEditor<Boolean> {

	private Consumer<Boolean> listener = p -> {
	};

	private final Property<Boolean> property;

	public BooleanPropertyEditor(Property<Boolean> property) {
		this.property = property;
		setSelected(property.getValue());
		property.bind(selectedProperty());
		selectedProperty().addListener((obs, old, val) -> listener.accept(val));
	}

	@Override
	public Property<? extends Boolean> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Boolean> consumer) {
		listener = listener.andThen(consumer);
	}
}
