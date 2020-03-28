package net.jamsimulator.jams.gui.theme.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.theme.Theme;

public class SelectedThemeChangeEvent extends Event {

	protected Theme oldTheme;
	protected Theme newTheme;

	SelectedThemeChangeEvent(Theme oldTheme, Theme newTheme) {
		this.oldTheme = oldTheme;
		this.newTheme = newTheme;
	}

	public Theme getOldTheme() {
		return oldTheme;
	}

	public Theme getNewTheme() {
		return newTheme;
	}

	public static class Before extends SelectedThemeChangeEvent implements Cancellable {

		private boolean cancelled;

		public Before(Theme oldTheme, Theme newTheme) {
			super(oldTheme, newTheme);
		}

		public void setNewTheme(Theme Theme) {
			this.newTheme = Theme;
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

	public static class After extends SelectedThemeChangeEvent {

		public After(Theme oldTheme, Theme newTheme) {
			super(oldTheme, newTheme);
		}

	}
}
