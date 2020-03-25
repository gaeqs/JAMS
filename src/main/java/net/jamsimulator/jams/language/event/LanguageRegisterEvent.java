package net.jamsimulator.jams.language.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.language.Language;

public class LanguageRegisterEvent extends Event {

	protected Language language;

	LanguageRegisterEvent(Language language) {
		this.language = language;
	}

	public Language getLanguage() {
		return language;
	}

	public static class Before extends LanguageRegisterEvent implements Cancellable {

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

	public static class After extends LanguageRegisterEvent {

		public After(Language language) {
			super(language);
		}

	}
}
