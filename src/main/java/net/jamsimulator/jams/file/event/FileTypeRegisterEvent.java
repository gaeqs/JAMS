package net.jamsimulator.jams.file.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.file.FileType;

public class FileTypeRegisterEvent extends Event {

	protected FileType fileType;

	FileTypeRegisterEvent(FileType fileType) {
		this.fileType = fileType;
	}

	public FileType getFileType() {
		return fileType;
	}

	public static class Before extends FileTypeRegisterEvent implements Cancellable {

		private boolean cancelled;

		public Before(FileType fileType) {
			super(fileType);
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

	public static class After extends FileTypeRegisterEvent {

		public After(FileType fileType) {
			super(fileType);
		}

	}
}
