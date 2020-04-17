package net.jamsimulator.jams.gui.action.event;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.action.Action;

public class ActionUnbindEvent extends Event {

	protected Action action;
	protected KeyCombination combination;

	ActionUnbindEvent(Action action, KeyCombination combination) {
		this.action = action;
		this.combination = combination;
	}

	public Action getAction() {
		return action;
	}

	public KeyCombination getCombination() {
		return combination;
	}

	public static class Before extends ActionUnbindEvent implements Cancellable {

		private boolean cancelled;

		public Before(Action action, KeyCombination combination) {
			super(action, combination);
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

	public static class After extends ActionUnbindEvent {

		public After(Action action, KeyCombination combination) {
			super(action, combination);
		}

	}
}
