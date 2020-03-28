package net.jamsimulator.jams.gui.theme.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.theme.Theme;

public class ThemeUnregisterEvent extends Event {

	protected Theme theme;

	ThemeUnregisterEvent(Theme theme) {
		this.theme = theme;
	}

	public Theme getTheme() {
		return theme;
	}

	public static class Before extends ThemeUnregisterEvent implements Cancellable {

		private boolean cancelled;

		public Before(Theme theme) {
			super(theme);
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

	public static class After extends ThemeUnregisterEvent {

		public After(Theme theme) {
			super(theme);
		}

	}
}
