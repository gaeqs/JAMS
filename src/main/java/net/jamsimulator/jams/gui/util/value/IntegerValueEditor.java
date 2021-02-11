package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.IntegerValueConverter;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.function.Consumer;

public class IntegerValueEditor extends TextField implements ValueEditor<Integer> {

	public static final String NAME = IntegerValueConverter.NAME;

	protected String oldText;

	private Consumer<Integer> listener = architecture -> {
	};

	public IntegerValueEditor() {
		setText("0");
		oldText = getText();

		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				int number = NumericUtils.decodeInteger(getText());

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
		setText(String.valueOf(value));
		listener.accept(value);
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
	public Node buildConfigNode(Label label) {
		return new HBox(label, this);
	}

	@Override
	public void addListener(Consumer<Integer> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<Integer> {

		@Override
		public ValueEditor<Integer> build() {
			return new IntegerValueEditor();
		}

	}
}
