package net.jamsimulator.jams.file;

import net.jamsimulator.jams.gui.display.AssemblyFileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;

public class AssemblyFileType extends FileType {


	/**
	 * Creates a text file type.
	 *
	 * @param name       the name.
	 * @param iconName   the name of the icon.
	 * @param iconPath   the path of the icon.
	 * @param extensions the extensions.
	 * @see net.jamsimulator.jams.gui.icon.IconManager
	 */
	public AssemblyFileType(String name, String iconName, String iconPath, String... extensions) {
		super(name, iconName, iconPath, extensions);
	}

	@Override
	public FileDisplay createDisplayTab(FileDisplayTab tab) {
		return new AssemblyFileDisplay(tab);
	}
}
