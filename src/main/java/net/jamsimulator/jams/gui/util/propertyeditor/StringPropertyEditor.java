package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringPropertyEditor extends TextField implements PropertyEditor<String> {

	private final Property<String> property;

	public StringPropertyEditor(Property<String> property) {
		this.property = property;
		setText(property.getValue());
		setOnAction(event -> property.setValue(getText()));
		focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			property.setValue(getText());
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
}
