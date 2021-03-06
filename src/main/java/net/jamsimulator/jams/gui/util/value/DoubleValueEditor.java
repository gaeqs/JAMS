package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.jamsimulator.jams.gui.util.converter.DoubleValueConverter;

import java.util.function.Consumer;

public class DoubleValueEditor extends TextField implements ValueEditor<Double> {

	public static final String NAME = DoubleValueConverter.NAME;

	protected String oldText;

	private Consumer<Double> listener = d -> {
	};

	public DoubleValueEditor() {
		setText("0.0");
		oldText = getText();

		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				double number = Double.parseDouble(getText());

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
	}

	@Override
	public void setCurrentValue(Double value) {
		setText(String.valueOf(value));
		listener.accept(value);
	}

	@Override
	public Double getCurrentValue() {
		return Double.valueOf(getText());
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Double> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<Double> {

		@Override
		public ValueEditor<Double> build() {
			return new DoubleValueEditor();
		}

	}
}
