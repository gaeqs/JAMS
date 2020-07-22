package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class FloatPropertyEditor extends TextField implements PropertyEditor<Float> {

	private final Property<Float> property;
	private String oldText;

	public FloatPropertyEditor(Property<Float> property) {
		this.property = property;

		setText(property.getValue().toString());

		oldText = getText();
		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				float number = Float.parseFloat(getText());
				property.setValue(number);
				oldText = getText();
			} catch (NumberFormatException ex) {
				setText(oldText);
			}
		};

		setOnAction(event -> run.run());
		focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			run.run();
		});
		setPrefWidth(45);
	}

	@Override
	public Property<? extends Float> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}
}
