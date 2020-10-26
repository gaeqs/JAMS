package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.language.wrapper.LanguagePieChartData;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Stats tab of the cache visualizer.
 */
public class CacheStatsVisualizer extends AnchorPane {

	private final CacheVisualizer visualizer;

	private final PieChart.Data hitRate;
	private final PieChart.Data missRate;

	private final Button resetButton;
	private final Label operationsCount, hitsCount, missesCount;

	public CacheStatsVisualizer(CacheVisualizer visualizer) {
		this.visualizer = visualizer;
		this.hitRate = new PieChart.Data("Hit rate", 0);
		this.missRate = new PieChart.Data("Miss rate", 0);

		getChildren().addAll(
				new LanguagePieChartData(hitRate, Messages.CACHES_HITS),
				new LanguagePieChartData(missRate, Messages.CACHES_MISSES));

		//Chart creation.
		var chart = new PieChart();
		chart.getData().addAll(hitRate, missRate);
		chart.setLegendVisible(false);
		AnchorUtils.setAnchor(chart, 0, 70, 0, 0);
		getChildren().add(chart);

		//Stats.
		operationsCount = new Label();
		hitsCount = new Label();
		missesCount = new Label();
		loadStats();

		//Reset button.
		resetButton = new LanguageButton(Messages.CACHES_RESET);
		loadResetButton();

		//Refresh all data
		refresh();
	}

	/**
	 * Called when the simulation is started.
	 */
	void onStart() {
		resetButton.setDisable(true);
	}

	/**
	 * Called when the simulation is stopped or reset.
	 */
	void onStop() {
		resetButton.setDisable(false);
	}

	/**
	 * Refreshes the data of the stats visualizer.
	 */
	void refresh() {
		Platform.runLater(() -> {
			var cache = visualizer.getSelectedCache();
			var stats = cache.getStats();
			var rate = stats.getHits() / (double) stats.getOperations();
			if (Double.isNaN(rate)) rate = 1;
			hitRate.setPieValue(rate);
			missRate.setPieValue(1 - rate);

			operationsCount.setText(String.valueOf(stats.getOperations()));
			hitsCount.setText(String.valueOf(stats.getHits()));
			missesCount.setText(String.valueOf(stats.getMisses()));
		});
	}

	/**
	 * Loads the stats section of the visualizer.
	 */
	private void loadStats() {
		var operations = new LanguageLabel(Messages.CACHES_STATS_OPERATIONS);
		var hits = new LanguageLabel(Messages.CACHES_STATS_HITS);
		var misses = new LanguageLabel(Messages.CACHES_STATS_MISSES);

		var opHBox = new HBox(operations, operationsCount);
		var hitsHBox = new HBox(hits, hitsCount);
		var missesHBox = new HBox(misses, missesCount);
		opHBox.setSpacing(10);
		hitsHBox.setSpacing(10);
		missesHBox.setSpacing(10);

		hits.prefWidthProperty().bind(operations.widthProperty());
		misses.prefWidthProperty().bind(operations.widthProperty());


		AnchorUtils.setAnchor(opHBox, -1, 70, 10, 0);
		AnchorUtils.setAnchor(hitsHBox, -1, 55, 10, 0);
		AnchorUtils.setAnchor(missesHBox, -1, 40, 10, 0);

		getChildren().add(opHBox);
		getChildren().add(hitsHBox);
		getChildren().add(missesHBox);
	}

	/**
	 * Loads the reset button.
	 */
	private void loadResetButton() {
		AnchorUtils.setAnchor(resetButton, -1, 5, 5, 5);
		resetButton.setOnAction(event -> {
			visualizer.getSimulation().resetCaches();
			refresh();
		});
		getChildren().add(resetButton);
	}
}
