package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
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

	private final HashMap<Cache, List<CacheOperationEvent>> messages;
	private final CacheVisualizer visualizer;

	private final VBox contents;
	private final Button clearButton, clearAllButton;

	public CacheLogVisualizer(CacheVisualizer visualizer) {
		contents = new VBox();
		contents.setFillWidth(true);
		messages = new HashMap<>();
		this.visualizer = visualizer;

		clearButton = new LanguageButton(Messages.CACHES_LOG_CLEAR);
		clearAllButton = new LanguageButton(Messages.CACHES_LOG_CLEAR_ALL);
		clearButton.setOnAction(event -> {
			contents.getChildren().clear();
			messages.remove(visualizer.getSelectedCache());
		});
		clearAllButton.setOnAction(event -> {
			contents.getChildren().clear();
			messages.clear();
		});

		clearButton.prefWidthProperty().bind(visualizer.widthProperty().divide(2));
		clearAllButton.prefWidthProperty().bind(clearButton.widthProperty());
		var buttonsHBox = new HBox(clearButton, clearAllButton);
		buttonsHBox.setSpacing(2);

		var scroll = new PixelScrollPane(contents);
		scroll.setFitToHeight(true);
		scroll.setFitToWidth(true);

		AnchorUtils.setAnchor(buttonsHBox, 0, -1, 5, 5);
		AnchorUtils.setAnchor(scroll, 30, 0, 0, 0);

		getChildren().addAll(scroll, buttonsHBox);
	}

	void onStart() {
		clearButton.setDisable(true);
		clearAllButton.setDisable(true);
	}

	void onStop() {
		clearButton.setDisable(false);
		clearAllButton.setDisable(false);
	}

	//Even register, for colored table.
	private boolean even = true;

	public void manageCacheEvent(CacheOperationEvent event) {
		var list = messages.computeIfAbsent(event.getCache(), k -> new LinkedList<>());
		list.add(event);
		if (list.size() > MAX_LOGS) {
			list.remove(0);
		}
	}

	public void refresh() {
		Platform.runLater(() -> {
			contents.getChildren().clear();
			var list = messages.get(visualizer.getSelectedCache());
			if (list == null) return;
			list.forEach(event -> {
				contents.getChildren().add(new CacheLogMessage(event, even = !even));
			});
		});
	}

	private static class CacheLogMessage extends VBox {

		public CacheLogMessage(CacheOperationEvent event, boolean even) {
			getStyleClass().addAll("cache-log-message", even ? "cache-log-message-even" : "cache-log-message-odd");
			loadMessage(event);
		}

		private void loadMessage(CacheOperationEvent event) {
			var hitOrMiss = new Label("■ " + event.getOperation());
			//I guess they never miss.
			hitOrMiss.getStyleClass().add(event.isHit() ? "hit" : "miss");
			hitOrMiss.setTooltip(new LanguageTooltip(event.isHit() ? Messages.CACHES_LOG_HIT : Messages.CACHES_LOG_MISS,
					"{OPERATION}", String.valueOf(event.getOperation())));
			var tag = event.getOldBlock() == null ? "-" : "0x"
					+ StringUtils.addZeros(Integer.toHexString(event.getOldBlock().getTag()), 8);
			var label = new LanguageLabel(Messages.CACHES_LOG_INDEX, "{INDEX}",
					String.valueOf(event.getBlockIndex()), "{TAG}", tag);

			var hBox = new HBox(new Group(hitOrMiss), new Group(label));
			hBox.setSpacing(20);

			if (event.isHit()) {
				getChildren().addAll(hBox);
			} else {
				var change = new Label();

				if (event.getNewBlock() != null) {
					if (event.getOldBlock() != null) {
						change.setText("\t0x" + StringUtils.addZeros(Integer.toHexString(event.getOldBlock().getTag()), 8)
								+ " -> 0x" + StringUtils.addZeros(Integer.toHexString(event.getNewBlock().getTag()), 8));
					} else {
						change.setText("\t-> 0x" + StringUtils.addZeros(Integer.toHexString(event.getNewBlock().getTag()), 8));
					}
				}

				getChildren().addAll(hBox, new Group(change));
			}
		}
	}
}
