package net.jamsimulator.jams.language.wrapper;

import javafx.scene.control.ListCell;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

public class CacheBuilderLanguageListCell extends ListCell<CacheBuilder<?>> {

	private String node;

	public CacheBuilderLanguageListCell() {
		node = null;
		Jams.getLanguageManager().registerListeners(this, true);
		refreshMessage();

		itemProperty().addListener((obs, old, val) -> {
			setNode(val == null ? null : val.getLanguageNode());
		});
	}

	public void setNode(String node) {
		this.node = node;
		refreshMessage();
	}

	private void refreshMessage() {
		if (node == null) return;
		setText(Jams.getLanguageManager().getSelected().getOrDefault(node));
	}

	@Listener
	public void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
		refreshMessage();
	}

	@Listener
	public void onDefaultLanguageChange(DefaultLanguageChangeEvent.After event) {
		refreshMessage();
	}

}
