package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class LongPropertyEditor extends TextField implements PropertyEditor<Long> {

	private Consumer<Long> listener = p -> {
	};

	private final Property<Long> property;
	private String oldText;

	public LongPropertyEditor(Property<Long> property) {
		this.property = property;

		setText(property.getValue().toString());

		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				long number = Long.parseLong(getText());
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
	public Property<? extends Long> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Long> consumer) {
		listener = listener.andThen(consumer);
	}
}
