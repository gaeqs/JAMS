package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.utils.AnchorUtils;
import net.jamsimulator.jams.utils.Validate;

public class CacheVisualizer extends AnchorPane {

	private final PieChart chart;
	private final PieChart.Data hitRate;
	private final PieChart.Data missRate;
	private Cache cache;

	public CacheVisualizer(Simulation<?> simulation, Cache cache) {
		Validate.notNull(cache, "Cache cannot be null!");
		this.cache = cache;

		this.chart = new PieChart();
		this.hitRate = new PieChart.Data("Hit rate", 0);
		this.missRate = new PieChart.Data("Miss rate", 0);

		chart.getData().addAll(hitRate, missRate);
		refresh();

		AnchorUtils.setAnchor(chart, 0, 30, 0, 0);
		getChildren().add(chart);


		var resetButton = new Button("Reset");
		AnchorUtils.setAnchor(resetButton, -1, 5, 5, 5);
		resetButton.setOnAction(event -> {
			simulation.resetCaches();
			refresh();
		});
		getChildren().add(resetButton);

		simulation.registerListeners(this, true);
	}

	public void setCache(Cache cache) {
		Validate.notNull(cache, "Cache cannot be null!");
		this.cache = cache;
		refresh();
	}

	private void refresh() {
		var stats = cache.getStats();
		var rate = stats.getHits() / (double) stats.getOperations();
		if (Double.isNaN(rate)) rate = 1;
		System.out.println("RATE: " + rate + " - " + stats);
		hitRate.setPieValue(rate);
		missRate.setPieValue(1 - rate);
		System.out.println("REFRESHING");
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		refresh();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		refresh();
	}

	@Listener
	private void onSimulationReset(SimulationResetEvent event) {
		refresh();
	}


}
