package net.jamsimulator.jams.gui.util;

import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.wrapper.CacheLanguageListCell;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderUnregisterEvent;

public class CacheBuilderComboBox extends ComboBox<CacheBuilder<?>> {

	public CacheBuilderComboBox(CacheBuilder<?> selected) {
		getItems().addAll(Jams.getCacheBuilderManager().getAll());
		getSelectionModel().select(selected);

		setCellFactory(list -> new CacheLanguageListCell());
		setButtonCell(new CacheLanguageListCell());

		Jams.getCacheBuilderManager().registerListeners(this, true);
	}

	@Listener
	private void onRegister(CacheBuilderRegisterEvent.After event) {
		getItems().add(event.getCacheBuilder());
	}

	@Listener
	private void onUnregister(CacheBuilderUnregisterEvent.After event) {
		if (event.getCacheBuilder().equals(getSelectionModel().getSelectedItem())) {
			getSelectionModel().select(Jams.getCacheBuilderManager().getAll().stream().findAny().get());
		}
		getItems().remove(event.getCacheBuilder());
	}


}
