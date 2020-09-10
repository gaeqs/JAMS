package net.jamsimulator.jams.gui.mips.simulator.flow.multicycle;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.event.MultiCycleStepEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MultiCycleFlowTable extends FlowTable {

	private LinkedList<MultiCycleStepEvent.After> toAdd;
	private Map<Long, MultiCycleFlowEntry> entries;

	private long firstCycle;

	public MultiCycleFlowTable(Simulation<? extends MultiCycleArchitecture> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		super(simulation, scrollPane, sizeSlider);

		firstCycle = 0;

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			entries = new HashMap<>();
			simulation.registerListeners(this, true);
		} else {
			setAlignment(Pos.CENTER);
			getChildren().add(new Label("Events are disabled."));
		}
	}

	@Override
	public Simulation<? extends MultiCycleArchitecture> getSimulation() {
		return (Simulation<? extends MultiCycleArchitecture>) super.getSimulation();
	}

	@Override
	public void setStepSize(double stepSize) {
		super.setStepSize(stepSize);
		refresh();
	}


	private void flushEvents() {
		if (toAdd.isEmpty()) return;
		String start = String.valueOf(simulation.getRegisters().getValidRegistersStarts().stream().findAny().get());

		MultiCycleStepEvent.After event;
		MultiCycleFlowEntry entry;
		while (!toAdd.isEmpty()) {
			event = toAdd.pop();
			entry = entries.get(event.getInstructionNumber());
			if (entry == null) {
				entry = new MultiCycleFlowEntry(getChildren().size(), scrollPane, this,
						event.getInstruction(), start, event.getInstructionNumber(), event.getCycle());
				entries.put(event.getInstructionNumber(), entry);
				getChildren().add(entry);
			}

			if (entries.size() > maxItems) {
				MultiCycleFlowEntry toRemove = (MultiCycleFlowEntry) getChildren().remove(0);
				entries.remove(toRemove.getInstructionNumber());
			}

			entry.addStep(event.getCycle(), event.getExecutedStep(), stepSize, firstCycle);
		}


		long localCycle = firstCycle;
		firstCycle = ((MultiCycleFlowEntry) getChildren().get(0)).getStartingCycle();

		if (localCycle != firstCycle) {
			refresh();
		}
	}

	@Listener
	private void onInstructionExecuted(MultiCycleStepEvent.After event) {
		//Adding items to a separate list prevents the app to block.
		toAdd.add(event);

		if (event.getInstructionNumber() - toAdd.getFirst().getInstructionNumber() >= maxItems) {
			long number = toAdd.removeFirst().getInstructionNumber();

			while (toAdd.getFirst().getInstructionNumber() == number) {
				toAdd.removeFirst();
			}

		}
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		Platform.runLater(this::flushEvents);
	}

	@Listener
	private void onSimulationReset(SimulationResetEvent event) {
		getChildren().clear();
		entries.clear();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		MultiCycleFlowEntry cycle = ((MultiCycleFlowEntry) getChildren().get(getChildren().size() - 1));
		if (cycle.removeCycle(event.getUndoCycle())) {
			entries.remove(cycle.getInstructionNumber());
			getChildren().remove(getChildren().size() - 1);
		}
	}

	private void refresh() {
		int index = 0;
		for (Node child : getChildren()) {
			if (child instanceof MultiCycleFlowEntry) {
				((MultiCycleFlowEntry) child).refresh(index++, stepSize, firstCycle);
			}
		}
	}
}
