package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.wrapper.SyscallLanguageListCell;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderUnregisterEvent;

public class SyscallBuilderComboBox extends ComboBox<SyscallExecutionBuilder<?>> {

	public SyscallBuilderComboBox(SyscallExecutionBuilder<?> selected) {
		getItems().addAll(Jams.getSyscallExecutionBuilderManager().getAll());
		getSelectionModel().select(selected);

		setCellFactory(list -> new SyscallLanguageListCell());
		setButtonCell(new SyscallLanguageListCell());

		Jams.getSyscallExecutionBuilderManager().registerListeners(this, true);
	}

	@Listener
	private void onRegister(SyscallExecutionBuilderRegisterEvent.After event) {
		getItems().add(event.getSyscallExecutionBuilder());
	}

	@Listener
	private void onUnregister(SyscallExecutionBuilderUnregisterEvent.After event) {
		if (event.getSyscallExecutionBuilder().equals(getSelectionModel().getSelectedItem())) {
			getSelectionModel().select(Jams.getSyscallExecutionBuilderManager()
					.get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
		}
		getItems().remove(event.getSyscallExecutionBuilder());
	}


}
