package net.jamsimulator.jams.language.wrapper;

import javafx.scene.control.ListCell;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.mips.memory.cache.Cache;

public class CacheLanguageListCell extends ListCell<Cache> {

	private String node;

	public CacheLanguageListCell() {
		node = null;
		Jams.getLanguageManager().registerListeners(this, true);
		refreshMessage();

		itemProperty().addListener((obs, old, val) -> {
			setNode(val == null ? null : val.getBuilder().getLanguageNode());
		});
	}

	public void setNode(String node) {
		this.node = node;
		refreshMessage();
	}

	private void refreshMessage() {
		if (node == null) return;

		var cache = itemProperty().get();
		var extra = cache.getBlocksAmount() + " / " + cache.getBlockSize();

		setText(Jams.getLanguageManager().getSelected().getOrDefault(node) + " " + extra);
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
