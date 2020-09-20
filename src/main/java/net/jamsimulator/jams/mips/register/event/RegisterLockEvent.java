package net.jamsimulator.jams.mips.register.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.register.Register;

/**
 * This event is called when a register is locked.
 */
public class RegisterLockEvent extends Event {

	protected final Register register;
	protected InstructionExecution<?, ?> execution;

	protected RegisterLockEvent(Register register, InstructionExecution<?, ?> execution) {
		this.register = register;
		this.execution = execution;
	}

	public Register getRegister() {
		return register;
	}

	public InstructionExecution<?, ?> getExecution() {
		return execution;
	}

	public static class Before extends RegisterLockEvent implements Cancellable {

		private boolean cancelled;

		public Before(Register register, InstructionExecution<?, ?> execution) {
			super(register, execution);
			this.cancelled = false;
		}

		public void setExecution(InstructionExecution<?, ?> execution) {
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

	public static class After extends RegisterLockEvent {

		public After(Register register, InstructionExecution<?, ?> execution) {
			super(register, execution);
		}
	}
}
