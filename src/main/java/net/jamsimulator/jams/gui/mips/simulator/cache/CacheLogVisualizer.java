package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.AnchorUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Log tab of the Cache Visualizer.
 */
public class CacheLogVisualizer extends AnchorPane {

	private final static int MAX_LOGS = 100;

	private final HashMap<Cache, List<CacheLogData>> messages;
	private final CacheVisualizer visualizer;

	private final ListView<CacheLogData> contents;
	private final Button clearButton, clearAllButton;

	private Cache currentCache;

	public CacheLogVisualizer(CacheVisualizer visualizer) {
		currentCache = visualizer.getSelectedCache();

		contents = new ListView<>(FXCollections.observableArrayList());
		contents.setCellFactory(list -> new CacheLogMessage());


		messages = new HashMap<>();
		this.visualizer = visualizer;

		clearButton = new LanguageButton(Messages.CACHES_LOG_CLEAR);
		clearAllButton = new LanguageButton(Messages.CACHES_LOG_CLEAR_ALL);
		clearButton.setOnAction(event -> messages.remove(visualizer.getSelectedCache()).clear());
		clearAllButton.setOnAction(event -> {
			messages.values().forEach(List::clear);
			messages.clear();
		});

		clearButton.prefWidthProperty().bind(visualizer.widthProperty().divide(2));
		clearAllButton.prefWidthProperty().bind(clearButton.widthProperty());
		var buttonsHBox = new HBox(clearButton, clearAllButton);
		buttonsHBox.setSpacing(2);

		AnchorUtils.setAnchor(buttonsHBox, 0, -1, 5, 5);
		AnchorUtils.setAnchor(contents, 30, 0, 0, 0);
		getChildren().addAll(contents, buttonsHBox);
	}

	/**
	 * Called when the simulation is started.
	 */
	void onStart() {
		clearButton.setDisable(true);
		clearAllButton.setDisable(true);
	}

	/**
	 * Called when the simulation is stopped or reset.
	 */
	void onStop() {
		clearButton.setDisable(false);
		clearAllButton.setDisable(false);
	}

	/**
	 * Adds the given {@link CacheOperationEvent} to the log.
	 *
	 * @param event the event.
	 */
	public void manageCacheEvent(CacheOperationEvent event) {
		var list = messages.computeIfAbsent(event.getCache(), k -> new LinkedList<>());
		list.add(new CacheLogData(event));
		if (list.size() > MAX_LOGS) {
			list.remove(0);
		}
	}

	/**
	 * Refreshes the data of the log visualizer.
	 */
	public void refresh() {
		Platform.runLater(() -> {
			currentCache = visualizer.getSelectedCache();
			var items = contents.getItems();
			items.clear();

			var data = messages.get(currentCache);
			if (data != null) {
				items.addAll(data);
			}
		});
	}

	private static class CacheLogData {

		private final long operation;
		private final boolean hit;
		private final int blockIndex;

		private final boolean hasOldBlock, hasNewBlock;
		private final int oldBlockTag, newBlockTag;


		public CacheLogData(CacheOperationEvent event) {
			this.operation = event.getOperation();
			this.hit = event.isHit();
			this.blockIndex = event.getBlockIndex();

			this.hasOldBlock = event.getOldBlock() != null;
			this.hasNewBlock = event.getNewBlock() != null;
			this.oldBlockTag = hasOldBlock ? event.getOldBlock().getTag() : 0;
			this.newBlockTag = hasNewBlock ? event.getNewBlock().getTag() : 0;
		}
	}

	private static class CacheLogMessage extends ListCell<CacheLogData> {

		@Override
		protected void updateItem(CacheLogData item, boolean empty) {
			super.updateItem(item, empty);
			if (item != null) {
				var box = new VBox();

				var hitOrMiss = new Label("■ " + item.operation);
				//I guess they never miss.
				hitOrMiss.getStyleClass().add(item.hit ? "hit" : "miss");
				hitOrMiss.setTooltip(new LanguageTooltip(item.hit ? Messages.CACHES_LOG_HIT : Messages.CACHES_LOG_MISS,
						"{OPERATION}", String.valueOf(item.operation)));


				//Index and tag message.
				var tag = !item.hasOldBlock
						? "-"
						: "0x" + StringUtils.addZeros(Integer.toHexString(item.oldBlockTag), 8);
				var label = new LanguageLabel(Messages.CACHES_LOG_INDEX, "{INDEX}",
						String.valueOf(item.blockIndex), "{TAG}", tag);

				var hBox = new HBox(new Group(hitOrMiss), new Group(label));
				hBox.setSpacing(20);

				if (item.hit) {
					box.getChildren().addAll(hBox);
				} else {
					//Calculates the change label.
					var change = new Label();
					if (item.hasNewBlock) {
						if (item.hasOldBlock) {
							change.setText("\t0x" + StringUtils.addZeros(Integer.toHexString(item.oldBlockTag), 8)
									+ " → 0x" + StringUtils.addZeros(Integer.toHexString(item.newBlockTag), 8));
						} else {
							change.setText("\t→ 0x" + StringUtils.addZeros(Integer.toHexString(item.newBlockTag), 8));
						}
					}

					box.getChildren().addAll(hBox, new Group(change));
				}


				setGraphic(box);
			} else {
				setGraphic(null);
			}
		}

		public CacheLogMessage() {
			getStyleClass().addAll("cache-log-message");
		}
	}
}
