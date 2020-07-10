package net.jamsimulator.jams.gui.util.log.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.utils.Validate;

public class ConsoleInputEvent extends Event {

	protected final Console console;
	protected String input;

	public ConsoleInputEvent(Console console, String input) {
		Validate.notNull(console, "Console cannot be null!");
		Validate.notNull(input, "Input cannot be null!");
		this.console = console;
		this.input = input;
	}

	public Console getConsole() {
		return console;
	}

	public String getInput() {
		return input;
	}


	public static class Before extends ConsoleInputEvent implements Cancellable {

		private boolean cancelled;

		public Before(Console console, String input) {
			super(console, input);
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		public void setInput(String input) {
			this.input = input;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}
	}

	public static class After extends ConsoleInputEvent {

		public After(Console console, String input) {
			super(console, input);
		}
	}
}
