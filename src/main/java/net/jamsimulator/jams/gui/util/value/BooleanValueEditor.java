package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import net.jamsimulator.jams.gui.util.converter.BooleanValueConverter;

import java.util.function.Consumer;

public class BooleanValueEditor extends CheckBox implements ValueEditor<Boolean> {

	public static final String NAME = BooleanValueConverter.NAME;

	private Consumer<Boolean> listener = b -> {
	};

	public BooleanValueEditor() {
		selectedProperty().addListener((obs, old, val) -> listener.accept(val));
	}

	@Override
	public void setCurrentValue(Boolean value) {
		setSelected(value);
	}

	@Override
	public Boolean getCurrentValue() {
		return isSelected();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Boolean> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<Boolean> {

		@Override
		public ValueEditor<Boolean> build() {
			return new BooleanValueEditor();
		}

	}
}
