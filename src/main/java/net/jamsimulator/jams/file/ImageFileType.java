package net.jamsimulator.jams.file;

import net.jamsimulator.jams.gui.display.CodeFileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.display.image.ImageFileDisplay;

public class ImageFileType extends FileType {


	/**
	 * Creates a image file type.
	 *
	 * @param name       the name.
	 * @param iconName   the name of the icon.
	 * @param iconPath   the path of the icon.
	 * @param extensions the extensions.
	 * @see net.jamsimulator.jams.gui.icon.IconManager
	 */
	public ImageFileType(String name, String iconName, String iconPath, String... extensions) {
		super(name, iconName, iconPath, extensions);
	}

	@Override
	public FileDisplay createDisplayTab(FileDisplayTab tab) {
		try {
			return new ImageFileDisplay(tab);
		} catch (Exception e) {
			System.err.println("Exception while opening image " + tab.getFile() + ".");
			e.printStackTrace();
			return null;
		}
	}
}
