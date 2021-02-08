package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderUnregisterEvent;

public class MemoryBuilderComboBox extends ComboBox<MemoryBuilder> {

	public MemoryBuilderComboBox(MemoryBuilder selected) {
		getItems().addAll(Jams.getMemoryBuilderManager());
		getSelectionModel().select(selected);

		setConverter(new StringConverter<MemoryBuilder>() {
			@Override
			public String toString(MemoryBuilder object) {
				return object.getName();
			}

			@Override
			public MemoryBuilder fromString(String string) {
				return Jams.getMemoryBuilderManager().get(string)
						.orElse(Jams.getMemoryBuilderManager().getDefault());
			}
		});
		Jams.getMemoryBuilderManager().registerListeners(this, true);
	}

	@Listener
	private void onRegister(MemoryBuilderRegisterEvent.After event) {
		getItems().add(event.getMemoryBuilder());
	}

	@Listener
	private void onUnregister(MemoryBuilderUnregisterEvent.After event) {
		if (event.getMemoryBuilder().equals(getSelectionModel().getSelectedItem())) {
			getSelectionModel().select(Jams.getMemoryBuilderManager().getDefault());
		}
		getItems().remove(event.getMemoryBuilder());
	}


}
