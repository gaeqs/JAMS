package net.jamsimulator.jams.language.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.language.Language;

public class LanguageUnregisterEvent extends Event {

	protected Language language;

	LanguageUnregisterEvent(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public static class Before extends LanguageUnregisterEvent implements Cancellable {

		private boolean cancelled;

		public Before(Language language) {
			super(language);
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

	public static class After extends LanguageUnregisterEvent {

		public After(Language language) {
			super(language);
		}

	}
}
