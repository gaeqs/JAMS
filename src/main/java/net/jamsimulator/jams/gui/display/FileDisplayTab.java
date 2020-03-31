package net.jamsimulator.jams.gui.display;

import javafx.scene.control.Tab;

import java.io.File;
import java.util.Objects;

public class FileDisplayTab extends Tab {

	private final FileDisplayList list;
	private final File file;
	private final FileDisplay display;

	public FileDisplayTab(FileDisplayList list, File file) {
		this.list = list;
		this.file = file;
		this.display = new FileDisplay(this);

		setText(file.getName());
		setContent(display);
	}

	public FileDisplayList getList() {
		return list;
	}

	public File getFile() {
		return file;
	}

	public FileDisplay getDisplay() {
		return display;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileDisplayTab that = (FileDisplayTab) o;
		return file.equals(that.file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}
}
