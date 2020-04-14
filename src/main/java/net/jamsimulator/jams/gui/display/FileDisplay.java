package net.jamsimulator.jams.gui.display;


public interface FileDisplay {

	FileDisplayTab getTab();

	void onClose();

	void save();

	void reload();
}
