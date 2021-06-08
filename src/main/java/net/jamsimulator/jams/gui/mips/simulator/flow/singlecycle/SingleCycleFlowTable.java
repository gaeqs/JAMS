package net.jamsimulator.jams.gui.mips.simulator.flow.singlecycle;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationUndoStepEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.event.SingleCycleInstructionExecutionEvent;

import java.util.LinkedList;

public class SingleCycleFlowTable extends FlowTable {

	private LinkedList<SingleCycleInstructionExecutionEvent.After> toAdd;

	public SingleCycleFlowTable(MIPSSimulation<? extends SingleCycleArchitecture> simulation) {
		super(simulation);

		if (simulation.getData().canCallEvents()) {
			toAdd = new LinkedList<>();
			simulation.registerListeners(this, true);
		} else {
			flows.setAlignment(Pos.CENTER);
			flows.getChildren().add(new Label("Events are disabled."));
		}
	}

	@Override
	public MIPSSimulation<? extends SingleCycleArchitecture> getSimulation() {
		return (MIPSSimulation<? extends SingleCycleArchitecture>) super.getSimulation();
	}

	@Override
	public long getFirstCycle() {
		if (flows.getChildren().isEmpty()) return 0;
		return ((SingleCycleFlowEntry) flows.getChildren().get(0)).getCycle();
	}

	@Override
	public long getLastCycle() {
		if (flows.getChildren().isEmpty()) return 0;
		return getFirstCycle() + flows.getChildren().size() - 1;
	}

	@Listener
	private void onInstructionExecuted(SingleCycleInstructionExecutionEvent.After event) {
		//Adding items to a separate list prevents the app to block.
		if (toAdd.size() == maxItems) {
			toAdd.remove(0);
		}
		toAdd.add(event);
	}

	@Listener
	private void onSimulationStop(SimulationStopEvent event) {
		Platform.runLater(() -> {
			if (toAdd.size() >= maxItems) {
				flows.getChildren().clear();
			} else if (flows.getChildren().size() + toAdd.size() > maxItems) {
				flows.getChildren().remove(0, flows.getChildren().size() + toAdd.size() - maxItems);

				int index = 0;
				for (Node child : flows.getChildren()) {
					if (child instanceof SingleCycleFlowEntry) {
						((SingleCycleFlowEntry) child).refresh(index++, stepSize);
					}
				}
			}


			String start = String.valueOf(simulation.getRegisters().getValidRegistersStarts().stream().findAny().get());
			SingleCycleInstructionExecutionEvent.After current;
			SingleCycleFlowEntry entry;
			while (!toAdd.isEmpty()) {
				current = toAdd.pop();
				entry = new SingleCycleFlowEntry(flows.getChildren().size(), this,
						current.getInstruction().orElse(null), start, current.getCycle(), stepSize);
				flows.getChildren().add(flows.getChildren().size(), entry);
			}
			refreshVisualizer();
		});
	}

	@Listener
	private void onSimulationReset(SimulationResetEvent event) {
		flows.getChildren().clear();
		refreshVisualizer();
	}

	@Listener
	private void onSimulationUndo(SimulationUndoStepEvent.After event) {
		Platform.runLater(() -> {
			flows.getChildren().remove(flows.getChildren().size() - 1);
			refreshVisualizer();
		});
	}

	@Override
	public void setStepSize(double stepSize) {
		super.setStepSize(stepSize);
		int index = 0;
		for (Node child : flows.getChildren()) {
			if (child instanceof SingleCycleFlowEntry) {
				((SingleCycleFlowEntry) child).refresh(index++, stepSize);
			}
		}
	}
}
