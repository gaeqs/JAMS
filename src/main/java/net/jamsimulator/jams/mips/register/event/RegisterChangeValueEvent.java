package net.jamsimulator.jams.mips.register.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.register.Register;

/**
 * This event is called when a register changes its value.
 */
public class RegisterChangeValueEvent extends Event {

	protected final Register register;
	protected final int oldValue;
	protected int newValue;

	protected RegisterChangeValueEvent(Register register, int oldValue, int newValue) {
		this.register = register;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Register getRegister() {
		return register;
	}

	public int getOldValue() {
		return oldValue;
	}

	public int getNewValue() {
		return newValue;
	}

	public static class Before extends RegisterChangeValueEvent implements Cancellable {

		private boolean cancelled;

		public Before(Register register, int oldValue, int newValue) {
			super(register, oldValue, newValue);
			this.cancelled = false;
		}

		public void setNewValue(int newValue) {
			this.newValue = newValue;
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

	public static class After extends RegisterChangeValueEvent {

		public After(Register register, int oldValue, int newValue) {
			super(register, oldValue, newValue);
		}
	}
}
