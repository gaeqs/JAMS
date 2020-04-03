package net.jamsimulator.jams.file;

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.icon.IconManager;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a file type. A file type contains a name an a collection of all supported extensions.
 * <p>
 * Remember that if two file types contains the same extension and the're inside
 * the same manager some functions will cause unpredictable results.
 */
public abstract class FileType {

	public static final int IMAGE_SIZE = 16;


	private final String name;
	private final Set<String> extensions;

	private Image icon;
	private final String iconName;
	private final String iconPath;

	/**
	 * Creates a file type.
	 *
	 * @param name       the name.
	 * @param iconName   the name of the icon.
	 * @param iconPath   the path of the icon.
	 * @param extensions the extensions.
	 * @see IconManager
	 */
	public FileType(String name, String iconName, String iconPath, String... extensions) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.hasNoNulls(extensions, "There must be no null extensions!");
		this.name = name;
		this.extensions = new HashSet<>();
		this.extensions.addAll(Arrays.asList(extensions));

		this.iconName = iconName;
		this.iconPath = iconPath;
		//Lazy initialization.
		this.icon = null;
	}

	/**
	 * Returns the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a mutable collection with all extensions.
	 * <p>
	 * Remember that if two file types contains the same extension and the're inside
	 * the same manager some functions will cause unpredictable results.
	 * <p>
	 * Extensions are case-insensitive.
	 *
	 * @return the extensions.
	 */
	public Set<String> getExtensions() {
		return extensions;
	}

	/**
	 * Returns the given extension from this file type.
	 *
	 * @param extension the extension.
	 * @return whether the extension was removed.
	 * @see #getExtensions()
	 */
	public boolean removeExtension(String extension) {
		return extensions.removeIf(target -> target.equalsIgnoreCase(extension));
	}

	/**
	 * Returns whether this file type supports the given file extension.
	 *
	 * @param extension the extension
	 * @return whether this file type supports the given file extension.
	 */
	public boolean supportsExtension(String extension) {
		return extensions.stream().anyMatch(target -> target.equalsIgnoreCase(extension));
	}

	public Image getIcon() {
		if (icon == null) {
			IconManager manager = JamsApplication.getIconManager();
			icon = manager.getOrLoadSafe(iconName, iconPath, IMAGE_SIZE, IMAGE_SIZE).orElse(null);
		}
		return icon;
	}

	/**
	 * Creates a {@link FileDisplayTab} for the given {@link FileDisplayTab}.
	 *
	 * @param tab the {@link FileDisplayTab}.
	 * @return the {@link FileDisplay}.
	 */
	public abstract FileDisplay createDisplayTab(FileDisplayTab tab);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileType fileType = (FileType) o;
		return name.equals(fileType.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
