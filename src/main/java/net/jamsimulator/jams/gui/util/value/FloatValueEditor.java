package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.jamsimulator.jams.gui.util.converter.FloatValueConverter;

import java.util.function.Consumer;

public class FloatValueEditor extends TextField implements ValueEditor<Float> {

	public static final String NAME = FloatValueConverter.NAME;

	protected String oldText;

	private Consumer<Float> listener = f -> {
	};

	public FloatValueEditor() {
		setText("0.0");
		oldText = getText();

		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				float number = Float.parseFloat(getText());

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
	public void setCurrentValue(Float value) {
		setText(String.valueOf(value));
		listener.accept(value);
	}

	@Override
	public Float getCurrentValue() {
		return Float.valueOf(getText());
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Float> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<Float> {

		@Override
		public ValueEditor<Float> build() {
			return new FloatValueEditor();
		}

	}
}
