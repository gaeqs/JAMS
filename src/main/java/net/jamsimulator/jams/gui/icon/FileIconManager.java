package net.jamsimulator.jams.gui.icon;

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This manage is used to easily get icons for given files.
 */
public class FileIconManager {

	/**
	 * The file icon size.
	 */
	public static final int IMAGE_SIZE = 16;

	/**
	 * The instance of this manager.
	 */
	public static final FileIconManager INSTANCE = new FileIconManager();

	private Image folderIcon, unknownIcon;
	private Map<String, Image> icons;

	private FileIconManager() {
		this.icons = new HashMap<>();
		loadDefaults();
	}

	/**
	 * Returns the best icon for the given file.
	 *
	 * @param file the given file.
	 * @return the icon.
	 */
	public Image getImageByFile(File file) {
		Validate.notNull(file, "File cannot be null!");
		if (file.isDirectory()) return folderIcon;
		String name = file.getName();
		int lastIndex = name.lastIndexOf(".");
		if (lastIndex == -1 || lastIndex == name.length() - 1) return unknownIcon;
		String extension = name.substring(lastIndex + 1);

		return icons.getOrDefault(extension, unknownIcon);
	}

	private void loadDefaults() {
		IconManager ic = JamsApplication.getIconManager();
		folderIcon = ic.getOrLoadSafe(Icons.FILE_FOLDER, Icons.FILE_FOLDER_PATH, IMAGE_SIZE, IMAGE_SIZE).orElse(null);
		unknownIcon = ic.getOrLoadSafe(Icons.FILE_UNKNOWN, Icons.FILE_UNKNOWN_PATH, IMAGE_SIZE, IMAGE_SIZE).orElse(null);
		addIfNotNull("txt", ic.getOrLoadSafe(Icons.FILE_TEXT, Icons.FILE_TEXT_PATH, IMAGE_SIZE, IMAGE_SIZE).orElse(null));
		addIfNotNull("asm", ic.getOrLoadSafe(Icons.FILE_ASSEMBLY, Icons.FILE_ASSEMBLY_PATH, IMAGE_SIZE, IMAGE_SIZE).orElse(null));
	}

	private void addIfNotNull(String extension, Image image) {
		if (image != null) icons.put(extension, image);
	}

}
