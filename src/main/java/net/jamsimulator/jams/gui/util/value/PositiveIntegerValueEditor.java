package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.function.Consumer;

public class PositiveIntegerValueEditor extends TextField implements ValueEditor<Integer> {

	public static final String NAME = "positive_integer";

	protected String oldText;

	private Consumer<Integer> listener = i -> {
	};

	public PositiveIntegerValueEditor() {
		setText("0");
		oldText = getText();

		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				int number = NumericUtils.decodeInteger(getText());
				if (number < 0) {
					setText(oldText);
					return;
				}

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
	public void setCurrentValue(Integer value) {
		int positive = Math.max(value, 0);
		setText(String.valueOf(positive));
		listener.accept(positive);
	}

	@Override
	public Integer getCurrentValue() {
		return Integer.valueOf(getText());
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Integer> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<Integer> {

		@Override
		public ValueEditor<Integer> build() {
			return new PositiveIntegerValueEditor();
		}

	}
}
