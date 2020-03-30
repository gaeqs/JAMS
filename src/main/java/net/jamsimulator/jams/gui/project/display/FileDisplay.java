package net.jamsimulator.jams.gui.project.display;


import javafx.scene.control.TextArea;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class FileDisplay extends TextArea {

	private final FileDisplayTab tab;

	public FileDisplay(FileDisplayTab tab) {
		this.tab = tab;
		read();
	}

	public FileDisplayTab getTab() {
		return tab;
	}

	private void read() {
		try {
			setText(FileUtils.readAll(tab.getFile()));
		} catch (IOException ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			setText(writer.toString());
		}
	}
}
