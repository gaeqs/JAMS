package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureRegisterEvent;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureUnregisterEvent;

import java.util.function.Consumer;

public class ArchitectureValueEditor extends ComboBox<Architecture> implements ValueEditor<Architecture> {

	public static final String NAME = ActionValueConverter.NAME;

	private Consumer<Architecture> listener = architecture -> {
	};

	public ArchitectureValueEditor() {
		setConverter(ValueConverters.getByTypeUnsafe(Architecture.class));
		getItems().addAll(Jams.getArchitectureManager().getAll());
		getSelectionModel().select(Jams.getArchitectureManager().getDefault());
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
		Jams.getArchitectureManager().registerListeners(this, true);
	}

	@Override
	public void setCurrentValue(Architecture value) {
		getSelectionModel().select(value);
	}

	@Override
	public Architecture getCurrentValue() {
		return getValue();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<Architecture> consumer) {
		listener = listener.andThen(consumer);
	}

	@Listener
	private void onArchitectureRegister(ArchitectureRegisterEvent.After event) {
		getItems().add(event.getArchitecture());
	}

	@Listener
	private void onArchitectureUnregister(ArchitectureUnregisterEvent.After event) {
		if (getSelectionModel().getSelectedItem().equals(event.getArchitecture()))
			setValue(Jams.getArchitectureManager().getDefault());
		getItems().remove(event.getArchitecture());
	}

	public static class Builder implements ValueEditor.Builder<Architecture> {

		@Override
		public ValueEditor<Architecture> build() {
			return new ArchitectureValueEditor();
		}

	}
}
