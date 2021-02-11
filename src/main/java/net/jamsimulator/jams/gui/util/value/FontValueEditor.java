package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

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
		return new HBox(label, this);
	}

	@Override
	public void addListener(Consumer<String> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<String> {

		@Override
		public ValueEditor<String> build() {
			return new FontValueEditor();
		}

	}
}
