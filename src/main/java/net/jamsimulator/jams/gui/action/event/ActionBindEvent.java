package net.jamsimulator.jams.gui.action.event;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.action.Action;

import java.util.Collections;
import java.util.Map;

public class ActionBindEvent extends Event {

	protected Action action;
	protected KeyCombination combination;
	protected Map<String, Action> replacedActions;

	ActionBindEvent(Action action, KeyCombination combination, Map<String, Action> replacedActions) {
		this.action = action;
		this.combination = combination;
		this.replacedActions = replacedActions;
	}

	public KeyCombination getCombination() {
		return combination;
	}

	public Action getAction() {
		return action;
	}

	public Map<String, Action> getReplacedActions() {
		return Collections.unmodifiableMap(replacedActions);
	}

	public static class Before extends ActionBindEvent implements Cancellable {

		private boolean cancelled;

		public Before(Action action, KeyCombination combination, Map<String, Action> replacedActions) {
			super(action, combination, replacedActions);
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

	public static class After extends ActionBindEvent {

		public After(Action action, KeyCombination combination, Map<String, Action> replacedActions) {
			super(action, combination, replacedActions);
		}

	}
}
