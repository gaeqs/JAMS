package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class DoublePropertyEditor extends TextField implements PropertyEditor<Double> {

	private Consumer<Double> listener = p -> {
	};

	private final Property<Double> property;
	private String oldText;

	public DoublePropertyEditor(Property<Double> property) {
		this.property = property;

		setText(property.getValue().toString());

		oldText = getText();
		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				double number = Double.parseDouble(getText());
				property.setValue(number);
				listener.accept(number);
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
	public Property<? extends Double> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Double> consumer) {
		listener = listener.andThen(consumer);
	}
}
