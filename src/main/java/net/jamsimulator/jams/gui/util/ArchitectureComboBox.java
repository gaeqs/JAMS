package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureRegisterEvent;

public class ArchitectureComboBox extends ComboBox<Architecture> {

	public ArchitectureComboBox(Architecture selected) {
		getItems().addAll(Jams.getArchitectureManager().getAll());
		getSelectionModel().select(selected);

		setConverter(new StringConverter<Architecture>() {
			@Override
			public String toString(Architecture object) {
				return object.getName();
			}

			@Override
			public Architecture fromString(String string) {
				return Jams.getArchitectureManager().get(string)
						.orElse(Jams.getArchitectureManager().getDefault());
			}
		});
		Jams.getArchitectureManager().registerListeners(this, true);
	}

	@Listener
	private void onRegister(ArchitectureRegisterEvent.After event) {
		getItems().add(event.getArchitecture());
	}

	@Listener
	private void onUnregister(ArchitectureRegisterEvent.After event) {
		if (event.getArchitecture().equals(getSelectionModel().getSelectedItem())) {
			getSelectionModel().select(Jams.getArchitectureManager().getDefault());
		}
		getItems().remove(event.getArchitecture());
	}


}
