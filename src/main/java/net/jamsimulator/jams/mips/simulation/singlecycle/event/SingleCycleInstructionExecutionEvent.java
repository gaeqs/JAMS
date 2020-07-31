package net.jamsimulator.jams.mips.simulation.singlecycle.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.simulation.singlecycle.SingleCycleSimulation;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

public class SingleCycleInstructionExecutionEvent extends SingleCycleSimulationEvent {

	protected final long cycle;
	protected int instructionAddress;
	protected AssembledInstruction instruction;
	protected SingleCycleExecution<?> execution;

	SingleCycleInstructionExecutionEvent(SingleCycleSimulation simulation,
										 long cycle,
										 int instructionAddress,
										 AssembledInstruction instruction,
										 SingleCycleExecution<?> execution) {
		super(simulation);
		Validate.notNull(simulation, "Simulation cannot be null!");
		Validate.notNull(instruction, "Instruction cannot be null!");
		this.cycle = cycle;
		this.instructionAddress = instructionAddress;
		this.instruction = instruction;
		this.execution = execution;
	}

	public long getCycle() {
		return cycle;
	}

	public int getInstructionAddress() {
		return instructionAddress;
	}

	public AssembledInstruction getInstruction() {
		return instruction;
	}

	public Optional<SingleCycleExecution<?>> getExecution() {
		return Optional.ofNullable(execution);
	}

	public static class Before extends SingleCycleInstructionExecutionEvent implements Cancellable {

		private boolean cancelled;

		public Before(SingleCycleSimulation simulation,
					  long cycle,
					  int instructionAddress,
					  AssembledInstruction instruction,
					  SingleCycleExecution<?> execution) {
			super(simulation, cycle, instructionAddress, instruction, execution);
		}

		public void setSingleCycleExecution(SingleCycleExecution<?> execution) {
			this.execution = execution;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}
	}

	public static class After extends SingleCycleInstructionExecutionEvent {

		public After(SingleCycleSimulation simulation,
					 long cycle,
					 int instructionAddress,
					 AssembledInstruction instruction,
					 SingleCycleExecution<?> execution) {
			super(simulation, cycle, instructionAddress, instruction, execution);
			Validate.notNull(execution, "Execution cannot be null!");
		}

		public SingleCycleExecution<?> getDoneExecution() {
			return execution;
		}
	}

}
