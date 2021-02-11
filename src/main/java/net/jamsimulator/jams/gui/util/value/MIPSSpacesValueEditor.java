package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.util.converter.MIPSSpacesValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class MIPSSpacesValueEditor extends ComboBox<MIPSSpaces> implements ValueEditor<MIPSSpaces> {

	public static final String NAME = MIPSSpacesValueConverter.NAME;

	private Consumer<MIPSSpaces> listener = mipsSpaces -> {
	};

	public MIPSSpacesValueEditor() {
		setCellFactory(param -> new MIPSSpacesListCell());
		setButtonCell(new MIPSSpacesListCell());
		setConverter(ValueConverters.getByTypeUnsafe(MIPSSpaces.class));
		getItems().addAll(MIPSSpaces.values());
		getSelectionModel().select(0);
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
	}

	@Override
	public void setCurrentValue(MIPSSpaces value) {
		getSelectionModel().select(value);
	}

	@Override
	public MIPSSpaces getCurrentValue() {
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
	public void addListener(Consumer<MIPSSpaces> consumer) {
		listener = listener.andThen(consumer);
	}

	public static class Builder implements ValueEditor.Builder<MIPSSpaces> {

		@Override
		public ValueEditor<MIPSSpaces> build() {
			return new MIPSSpacesValueEditor();
		}

	}


	private static class MIPSSpacesListCell extends ListCell<MIPSSpaces> {

		public MIPSSpacesListCell() {
			itemProperty().addListener((obs, old, val) -> setText(val == null ? null : val.getDisplayValue()));
		}

	}
}
