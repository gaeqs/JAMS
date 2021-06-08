package net.jamsimulator.jams.gui.mips.simulator.flow.multicycle;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.SegmentedFlowEntry;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.event.MultiCycleStepEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MultiCycleFlowTable extends FlowTable {

	private LinkedList<MultiCycleStepEvent.After> toAdd;
	private Map<Long, SegmentedFlowEntry> entries;

	private long firstCycle;

	public MultiCycleFlowTable(MIPSSimulation<? extends MultiCycleArchitecture> simulation) {
		super(simulation);

		firstCycle = 0;

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			entries = new HashMap<>();
			simulation.registerListeners(this, true);
		} else {
			flows.setAlignment(Pos.CENTER);
			flows.getChildren().add(new Label("Events are disabled."));
		}
	}

	@Override
	public MIPSSimulation<? extends MultiCycleArchitecture> getSimulation() {
		return (MIPSSimulation<? extends MultiCycleArchitecture>) super.getSimulation();
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
		SegmentedFlowEntry entry;
		while (!toAdd.isEmpty()) {
			event = toAdd.pop();
			entry = entries.get(event.getInstructionNumber());
			if (entry == null) {
				entry = new SegmentedFlowEntry(flows.getChildren().size(), this,
						event.getInstruction(), start, event.getInstructionNumber(), event.getCycle());
				entries.put(event.getInstructionNumber(), entry);
				flows.getChildren().add(entry);
			}

			if (entries.size() > maxItems) {
				SegmentedFlowEntry toRemove = (SegmentedFlowEntry) flows.getChildren().remove(0);
				entries.remove(toRemove.getInstructionNumber());
			}

			entry.addStep(event.getCycle(), event.getExecutedStep(), stepSize, firstCycle, false);
		}


		long localCycle = firstCycle;
		firstCycle = ((SegmentedFlowEntry) flows.getChildren().get(0)).getStartingCycle();

		if (localCycle != firstCycle) {
			refresh();
		}

		refreshVisualizer();
	}

	@Override
	public long getFirstCycle() {
		return firstCycle;
	}


	@Override
	public long getLastCycle() {
		if (flows.getChildren().isEmpty()) return 0;

		var last = (SegmentedFlowEntry) flows.getChildren().get(flows.getChildren().size() - 1);
		return last.getLastCycle();
	}

	public void refresh() {
		int index = 0;
		for (Node child : flows.getChildren()) {
			if (child instanceof SegmentedFlowEntry) {
				((SegmentedFlowEntry) child).refresh(index++, stepSize, firstCycle);
			}
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
		flows.getChildren().clear();
		entries.clear();
		refreshVisualizer();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		SegmentedFlowEntry cycle = ((SegmentedFlowEntry) flows.getChildren().get(flows.getChildren().size() - 1));
		if (cycle.removeCycle(event.getUndoCycle()) && cycle.isEmpty()) {
			entries.remove(cycle.getInstructionNumber());
			flows.getChildren().remove(flows.getChildren().size() - 1);
		}
		refreshVisualizer();
	}
}
