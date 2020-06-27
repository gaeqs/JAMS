package net.jamsimulator.jams.gui.util;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.wrapper.SyscallLanguageListCell;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderUnregisterEvent;
import net.jamsimulator.jams.utils.NumericStringComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SyscallBuilderComboBox extends ComboBox<SyscallExecutionBuilder<?>> {

	public SyscallBuilderComboBox(SyscallExecutionBuilder<?> selected) {
		getItems().addAll(Jams.getSyscallExecutionBuilderManager().getAll());
		getSelectionModel().select(selected);

		setCellFactory(list -> new SyscallLanguageListCell());
		setButtonCell(new SyscallLanguageListCell());
		sort();

		Jams.getSyscallExecutionBuilderManager().registerListeners(this, true);
		Jams.getLanguageManager().registerListeners(this, true);
	}

	@Listener
	private void onRegister(SyscallExecutionBuilderRegisterEvent.After event) {
		getItems().add(event.getSyscallExecutionBuilder());
		sort();
	}

	@Listener
	private void onUnregister(SyscallExecutionBuilderUnregisterEvent.After event) {
		if (event.getSyscallExecutionBuilder().equals(getSelectionModel().getSelectedItem())) {
			getSelectionModel().select(Jams.getSyscallExecutionBuilderManager()
					.get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
		}
		getItems().remove(event.getSyscallExecutionBuilder());
	}

	@Listener
	private void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
		Platform.runLater(() -> sort());
	}

	@Listener
	private void onDefaultLanguageChange(DefaultLanguageChangeEvent.After event) {
		Platform.runLater(() -> sort());
	}

	private void sort() {
		Language language = Jams.getLanguageManager().getSelected();
		SyscallExecutionBuilder<?> selected = getSelectionModel().getSelectedItem();
		List<SyscallExecutionBuilder<?>> items = new ArrayList<>(getItems());
		getItems().clear();
		items.sort(Comparator.comparing(target -> language.getOrDefault(target.getLanguageNode()),
				new NumericStringComparator()));
		getItems().addAll(items);
		getSelectionModel().select(selected);
	}

}
