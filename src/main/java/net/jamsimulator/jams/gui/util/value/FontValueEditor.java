package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class FontValueEditor extends ComboBox<String> implements ValueEditor<String> {

	public static final String NAME = "font";

	private Consumer<String> listener = font -> {
	};

	public FontValueEditor() {
		getItems().addAll(Font.getFamilies());
		getSelectionModel().select(0);
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
	}

	@Override
	public void setCurrentValue(String value) {
		getSelectionModel().select(value);
	}

	@Override
	public String getCurrentValue() {
		return getValue();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public Node buildConfigNode(Label label) {
		var box =  new HBox(label, this);
		box.setSpacing(5);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	@Override
	public void addListener(Consumer<String> consumer) {
		listener = listener.andThen(consumer);
	}

	@Override
	public ValueConverter<String> getLinkedConverter() {
		return ValueConverters.getByTypeUnsafe(String.class);
	}

	public static class Builder implements ValueEditor.Builder<String> {

		@Override
		public ValueEditor<String> build() {
			return new FontValueEditor();
		}

	}
}
