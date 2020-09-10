package net.jamsimulator.jams.gui.mips.simulator.flow.pipelined;

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
import net.jamsimulator.jams.mips.simulation.pipelined.Pipeline;
import net.jamsimulator.jams.mips.simulation.pipelined.event.PipelineShiftEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PipelinedFlowTable extends FlowTable {

	private LinkedList<PipelineShiftEvent.Before> toAdd;
	private LinkedList<Pipeline> pipelines;
	private Map<Long, PipelinedFlowEntry> entries;

	private long firstCycle;

	public PipelinedFlowTable(Simulation<? extends MultiCycleArchitecture> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		super(simulation, scrollPane, sizeSlider);

		firstCycle = 0;

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			pipelines = new LinkedList<>();
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

		Pipeline pipeline;
		while (!toAdd.isEmpty()) {
			var event = toAdd.pop();
			pipeline = pipelines.pop();
			pipeline.getAll().forEach((step, instruction) -> {

				var entry = entries.get(instruction.getInstructionId());
				if (entry == null) {
					entry = new PipelinedFlowEntry(getChildren().size(), scrollPane, this, instruction.getInstruction(),
							start, instruction.getInstructionId(), event.getCycle());
					entries.put(instruction.getInstructionId(), entry);
					getChildren().add(entry);

					if (entries.size() > maxItems) {
						PipelinedFlowEntry toRemove = (PipelinedFlowEntry) getChildren().remove(0);
						entries.remove(toRemove.getInstructionNumber());
					}
				}

				entry.addStep(event.getCycle(), step, stepSize, firstCycle);
			});
		}


		long localCycle = firstCycle;
		firstCycle = ((PipelinedFlowEntry) getChildren().get(0)).getStartingCycle();

		if (localCycle != firstCycle) {
			refresh();
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
		getChildren().clear();
		entries.clear();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		var index = getChildren().size() - 1;
		PipelinedFlowEntry entry;
		while (index >= 0) {
			entry = ((PipelinedFlowEntry) getChildren().get(index));
			if (entry.removeCycle(event.getUndoCycle())) {
				if (entry.isEmpty()) {
					entries.remove(entry.getInstructionNumber());
					getChildren().remove(index);
				}
			}
			index--;
		}
	}

	private void refresh() {
		int index = 0;
		for (Node child : getChildren()) {
			if (child instanceof PipelinedFlowEntry) {
				((PipelinedFlowEntry) child).refresh(index++, stepSize, firstCycle);
			}
		}
	}
}
