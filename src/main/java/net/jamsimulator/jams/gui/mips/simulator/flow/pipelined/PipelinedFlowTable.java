package net.jamsimulator.jams.gui.mips.simulator.flow.pipelined;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.flow.SegmentedFlowEntry;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.pipelined.Pipeline;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PipelinedFlowTable extends FlowTable {

	private LinkedList<PipelineShiftEvent.Before> toAdd;
	private LinkedList<Pipeline> pipelines;
	private Map<Long, SegmentedFlowEntry> entries;

	private long firstCycle;

	public PipelinedFlowTable(Simulation<? extends MultiCycleArchitecture> simulation) {
		super(simulation);

		firstCycle = 0;

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			pipelines = new LinkedList<>();
			entries = new HashMap<>();
			simulation.registerListeners(this, true);
		} else {
			flows.setAlignment(Pos.CENTER);
			flows.getChildren().add(new Label("Events are disabled."));
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

		while (!toAdd.isEmpty()) {
			var event = toAdd.pop();
			var pipeline = pipelines.pop();
			pipeline.getAll().forEach((step, instruction) -> {

				var entry = entries.get(instruction.getInstructionId());
				if (entry == null) {
					entry = new SegmentedFlowEntry(flows.getChildren().size(), this, instruction.getInstruction(),
							start, instruction.getInstructionId(), event.getCycle());
					entries.put(instruction.getInstructionId(), entry);
					flows.getChildren().add(entry);

					if (entries.size() > maxItems) {
						SegmentedFlowEntry toRemove = (SegmentedFlowEntry) flows.getChildren().remove(0);
						entries.remove(toRemove.getInstructionNumber());
					}
				}

				var raw = 5 - event.getShiftAmount() > step.ordinal();
				entry.addStep(event.getCycle(), step, stepSize, firstCycle, raw);
			});
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

	@Listener(priority = Integer.MIN_VALUE)
	private void onInstructionExecuted(PipelineShiftEvent.Before event) {
		//Adding items to a separate list prevents the app to block.
		toAdd.add(event);
		pipelines.add(event.getPipeline().clone());

		if (toAdd.size() > maxItems << 2) {
			toAdd.removeFirst();
			pipelines.removeFirst();
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
		var index = flows.getChildren().size() - 1;
		SegmentedFlowEntry entry;
		while (index >= 0) {
			entry = ((SegmentedFlowEntry) flows.getChildren().get(index));
			if (entry.removeCycle(event.getUndoCycle()) && entry.isEmpty()) {
				entries.remove(entry.getInstructionNumber());
				flows.getChildren().remove(index);
			}
			index--;
		}
		refreshVisualizer();
	}
}
