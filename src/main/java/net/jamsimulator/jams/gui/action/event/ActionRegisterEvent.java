package net.jamsimulator.jams.gui.action.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.action.Action;

public class ActionRegisterEvent extends Event {

	protected Action action;

	ActionRegisterEvent(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public static class Before extends ActionRegisterEvent implements Cancellable {

		private boolean cancelled;

		public Before(Action action) {
			super(action);
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

	public static class After extends ActionRegisterEvent {

		public After(Action action) {
			super(action);
		}

	}
}
