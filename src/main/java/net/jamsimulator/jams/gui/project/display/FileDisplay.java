package net.jamsimulator.jams.gui.project.display;


import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class FileDisplay extends CodeArea {

	private final FileDisplayTab tab;

	public FileDisplay(FileDisplayTab tab) {
		super(read(tab));
		this.tab = tab;
	}

	public FileDisplayTab getTab() {
		return tab;
	}

	private static String read(FileDisplayTab tab) {
		try {
			return FileUtils.readAll(tab.getFile());
		} catch (IOException ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}
}
