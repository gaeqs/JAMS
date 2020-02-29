package net.jamsimulator.jams.language.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.language.Language;

public class SelectedLanguageChangeEvent extends Event {

	protected Language oldLanguage;
	protected Language newLanguage;

	SelectedLanguageChangeEvent(Language oldLanguage, Language newLanguage) {
		this.oldLanguage = oldLanguage;
		this.newLanguage = newLanguage;
	}

	public Language getOldLanguage() {
		return oldLanguage;
	}

	public Language getNewLanguage() {
		return newLanguage;
	}

	public static class Before extends SelectedLanguageChangeEvent implements Cancellable {

		private boolean cancelled;

		public Before(Language oldLanguage, Language newLanguage) {
			super(oldLanguage, newLanguage);
		}

		public void setNewLanguage(Language language) {
			this.newLanguage = language;
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

	public static class After extends SelectedLanguageChangeEvent {

		public After(Language oldLanguage, Language newLanguage) {
			super(oldLanguage, newLanguage);
		}

	}
}
