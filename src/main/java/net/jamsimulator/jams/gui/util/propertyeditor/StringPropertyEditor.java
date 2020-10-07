package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class StringPropertyEditor extends TextField implements PropertyEditor<String> {

	private Consumer<String> listener = p -> {
	};

	private final Property<String> property;

	public StringPropertyEditor(Property<String> property) {
		this.property = property;
		setText(property.getValue());
		setOnAction(event -> property.setValue(getText()));
		focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			property.setValue(getText());
			listener.accept(getText());
		});
		setPrefWidth(60);
	}

	@Override
	public Property<? extends String> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<String> consumer) {
		listener = listener.andThen(consumer);
	}
}
