package net.jamsimulator.jams.gui.mips.simulator.cache;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.CacheLanguageListCell;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Optional;

/**
 * Main class for the Cache Visualizer pane.
 */
public class CacheVisualizer extends AnchorPane {

	private final MIPSSimulation<?> simulation;
	private final ComboBox<Cache> cacheComboBox;
	private final CacheStatsVisualizer statsVisualizer;
	private final CacheLogVisualizer logVisualizer;

	/**
	 * Creates the cache visualizer.
	 *
	 * @param simulation the {@link MIPSSimulation} containing the cache.
	 */
	public CacheVisualizer(MIPSSimulation<?> simulation) {
		this.simulation = simulation;

		cacheComboBox = new ComboBox<>();
		loadCacheComboBox(simulation.getMemory());

		statsVisualizer = new CacheStatsVisualizer(this);
		logVisualizer = new CacheLogVisualizer(this);

		loadTabs();

		simulation.registerListeners(this, true);
	}

	/**
	 * Returns the {@link MIPSSimulation} containing the caches of this visualizer.
	 *
	 * @return the {@link MIPSSimulation}.
	 */
	public MIPSSimulation<?> getSimulation() {
		return simulation;
	}

	/**
	 * Returns the selected {@link Cache} of the visualizer.
	 *
	 * @return the selected {@link Cache}.
	 */
	public Cache getSelectedCache() {
		return cacheComboBox.getSelectionModel().getSelectedItem();
	}

	/**
	 * Loads the cache combo box.
	 * This box allows to select any cache of the simulation.
	 *
	 * @param memory the {@link MIPSSimulation}'s memory.
	 */
	private void loadCacheComboBox(Memory memory) {
		cacheComboBox.setCellFactory(f -> new CacheLanguageListCell());
		cacheComboBox.setButtonCell(new CacheLanguageListCell());

		Optional<Memory> current = Optional.of(memory);
		while (current.isPresent()) {
			if (current.get() instanceof Cache) {
				cacheComboBox.getItems().add((Cache) current.get());
				current.get().registerListeners(this, true);
			}
			current = current.get().getNextLevelMemory();
		}

		cacheComboBox.getSelectionModel().select(0);
		cacheComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> refresh());

		AnchorUtils.setAnchor(cacheComboBox, 0, -1, 5, 5);
		getChildren().add(cacheComboBox);
	}

	protected void loadTabs() {
		var tabPane = new TabPane();

		var statsScroll = new PixelScrollPane(statsVisualizer);
		statsScroll.setFitToWidth(true);
		statsScroll.setFitToHeight(true);
		var statsTab = new LanguageTab(Messages.CACHE_STATS, statsScroll);
		statsTab.setClosable(false);
		tabPane.getTabs().add(statsTab);


		var logTab = new LanguageTab(Messages.CACHE_LOG, logVisualizer);
		logTab.setClosable(false);
		tabPane.getTabs().add(logTab);

		AnchorUtils.setAnchor(tabPane, 30, 0, 0, 0);
		getChildren().add(tabPane);
	}

	private void refresh() {
		statsVisualizer.refresh();
		logVisualizer.refresh();
	}

	@Listener
	private void onSimulationStart(SimulationStartEvent event) {
		statsVisualizer.onStart();
		logVisualizer.onStart();
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		refresh();
		statsVisualizer.onStop();
		logVisualizer.onStop();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		refresh();
		statsVisualizer.onStop();
		logVisualizer.onStop();
	}

	@Listener
	private void onSimulationReset(SimulationResetEvent event) {
		refresh();
		statsVisualizer.onStop();
		logVisualizer.onStop();
	}

	@Listener
	private void onCacheOperation(CacheOperationEvent event) {
		logVisualizer.manageCacheEvent(event);
	}

}
