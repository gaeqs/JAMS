package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;

import java.util.LinkedList;

public class SingleCycleFlowTable extends FlowTable {

	private LinkedList<AssembledInstruction> toAdd;

	public SingleCycleFlowTable(Simulation<? extends SingleCycleArchitecture> simulation, ScrollPane scrollPane, Slider sizeSlider) {
		super(simulation, scrollPane, sizeSlider);

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			simulation.registerListeners(this, true);
		} else {
			setAlignment(Pos.CENTER);
			getChildren().add(new Label("Events are disabled."));
		}
	}

	@Override
	public Simulation<? extends SingleCycleArchitecture> getSimulation() {
		return (Simulation<? extends SingleCycleArchitecture>) super.getSimulation();
	}

	@Listener
	private void onInstructionExecuted(SingleCycleInstructionExecutionEvent.After event) {
		//Adding items to a separate list prevents the app to block.
		if (toAdd.size() == maxItems) {
			toAdd.remove(0);
		}
		toAdd.add(event.getInstruction());
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		Platform.runLater(() -> {
			if (toAdd.size() >= maxItems) {
				getChildren().clear();
			} else if (getChildren().size() + toAdd.size() > maxItems) {
				getChildren().remove(0, getChildren().size() + toAdd.size() - maxItems);

				int index = 0;
				for (Node child : getChildren()) {
					if (child instanceof SingleCycleFlowEntry) {
						((SingleCycleFlowEntry) child).refresh(index++, stepSize);
					}
				}
			}


			String start = String.valueOf(simulation.getRegisters().getValidRegistersStarts().stream().findAny().get());
			AssembledInstruction instruction;
			SingleCycleFlowEntry entry;
			while (!toAdd.isEmpty()) {
				instruction = toAdd.pop();
				entry = new SingleCycleFlowEntry(getChildren().size(), scrollPane, this,
						instruction, start, stepSize);
				getChildren().add(getChildren().size(), entry);
			}
		});
	}

	@Listener
	private void onSimulationReset(SimulationResetEvent event) {
		getChildren().clear();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		Platform.runLater(() -> getChildren().remove(getChildren().size() - 1));
	}

	@Override
	public void setStepSize(double stepSize) {
		super.setStepSize(stepSize);
		int index = 0;
		for (Node child : getChildren()) {
			if (child instanceof SingleCycleFlowEntry) {
				((SingleCycleFlowEntry) child).refresh(index++, stepSize);
			}
		}
	}
}
