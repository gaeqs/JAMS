package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.HashMap;

public class MemoryTable extends TableView<MemoryEntry> {

	private final HashMap<Integer, MemoryEntry> entries;

	protected Simulation<?> simulation;

	public MemoryTable(Simulation<?> simulation) {
		this.simulation = simulation;
		getStyleClass().add("table-view-horizontal-fit");
		setEditable(true);
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

		TableColumn<MemoryEntry, String> pAddress = new TableColumn<>("Address");
		TableColumn<MemoryEntry, String> p0 = new TableColumn<>("+0");
		TableColumn<MemoryEntry, String> p4 = new TableColumn<>("+4");
		TableColumn<MemoryEntry, String> p8 = new TableColumn<>("+8");
		TableColumn<MemoryEntry, String> pC = new TableColumn<>("+C");

		pAddress.setCellValueFactory(p -> p.getValue().addressProperty());
		p0.setCellValueFactory(p -> p.getValue().p0Property());
		p4.setCellValueFactory(p -> p.getValue().p4Property());
		p8.setCellValueFactory(p -> p.getValue().p8Property());
		pC.setCellValueFactory(p -> p.getValue().pCProperty());


		getColumns().setAll(pAddress, p0, p4, p8, pC);

		entries = new HashMap<>();

		int current = MIPS32Memory.HEAP;
		int end = current + 0xFFFF;
		MemoryEntry entry;
		while (current <= end) {
			entry = new MemoryEntry(simulation, current);
			entries.put(current, entry);
			getItems().add(entry);
			current += 0x10;
		}


		simulation.registerListeners(this, true);
		if (!simulation.isRunning()) {
			simulation.getMemory().registerListeners(this, true);
		}
	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	@Listener
	private void onSimulationStart(SimulationStartEvent event) {
		simulation.getMemory().unregisterListeners(this);
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		simulation.getMemory().registerListeners(this, true);
		Memory memory = event.getSimulation().getMemory();
		entries.values().forEach(entry -> entry.refresh(memory));
	}

	@Listener
	private void onMemoryChange(MemoryByteSetEvent.After event) {
		int offset = (event.getAddress() % 16) >> 2 << 2;
		int address = event.getAddress() >> 4 << 4;
		MemoryEntry entry = entries.get(address);
		if (entry == null) return;
		entry.update(event.getMemory().getWord(address + offset), offset, true);
	}


	@Listener
	private void onMemoryChange(MemoryWordSetEvent.After event) {
		int offset = event.getAddress() % 16;
		int address = event.getAddress() >> 4 << 4;
		MemoryEntry entry = entries.get(address);
		if (entry == null) return;
		entry.update(event.getValue(), offset, true);
	}

}
