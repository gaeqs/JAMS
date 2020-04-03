package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.file.AssemblyFileType;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.file.TextFileType;
import net.jamsimulator.jams.file.event.FileTypeRegisterEvent;
import net.jamsimulator.jams.file.event.FileTypeUnregisterEvent;
import net.jamsimulator.jams.gui.icon.Icons;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link FileType}s that JAMS may use.
 * <p>
 * To register a {@link FileType} use {@link #register(FileType)}.
 * To unregister a {@link FileType} use {@link #unregister(String)}.
 */
public class FileTypeManager extends SimpleEventBroadcast {

	public static final FileTypeManager INSTANCE = new FileTypeManager();

	private FileType unknownType, folderType;
	private final Set<FileType> fileTypes;

	private FileTypeManager() {
		fileTypes = new HashSet<>();
		loadDefaults();
	}

	/**
	 * Returns the unknown {@link FileType} instance. This {@link FileType} is not registered in
	 * this manager, and can be only returned by this method.
	 *
	 * @return the unknown {@link FileType}.
	 */
	public FileType getUnknownType() {
		return unknownType;
	}

	/**
	 * Returns the {@link FileType} representing folders. This {@link FileType} is not registered in
	 * this manager, but it's used by it in the method {@link #getByFile(File)}.
	 *
	 * @return the {@link FileType}.
	 */
	public FileType getFolderType() {
		return folderType;
	}

	/**
	 * Returns the {@link FileType} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link FileType}, if present.
	 */
	public Optional<FileType> get(String name) {
		return fileTypes.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns the {@link FileType} that supports the given extension, if present.
	 * <p>
	 * If two or more {@link FileType}s support the given extension, any of them will be returned.
	 *
	 * @param extension the extension.
	 * @return the {@link FileType}, if present.
	 */
	public Optional<FileType> getByExtension(String extension) {
		return fileTypes.stream().filter(target -> target.supportsExtension(extension)).findAny();
	}

	/**
	 * Returns that {@link FileType} that supports the extension of the given {@link File}, if present.
	 * <p>
	 * If two or more {@link FileType}s support the given extension, any of them will be returned.
	 *
	 * @param file the file.
	 * @return the {@link FileType}, if present.
	 */
	public Optional<FileType> getByFile(File file) {
		Validate.notNull(file, "File cannot be null!");

		if (file.isDirectory()) return Optional.of(folderType);

		String name = file.getName();
		int lastIndex = name.lastIndexOf(".");
		if (lastIndex == -1 || lastIndex == name.length()) return Optional.empty();
		String extension = name.substring(lastIndex + 1);
		return getByExtension(extension);
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link FileType}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<FileType> getAll() {
		return Collections.unmodifiableSet(fileTypes);
	}

	/**
	 * Attempts to register the given {@link FileType} into the manager.
	 * This will fail if a {@link FileType} with the same name already exists within this manager.
	 *
	 * @param fileType the fileType to register.
	 * @return whether the fileType was registered.
	 */
	public boolean register(FileType fileType) {
		Validate.notNull(fileType, "FileType cannot be null!");

		FileTypeRegisterEvent.Before before = callEvent(new FileTypeRegisterEvent.Before(fileType));
		if (before.isCancelled()) return false;
		boolean result = fileTypes.add(fileType);
		if (result) callEvent(new FileTypeRegisterEvent.After(fileType));
		return result;
	}

	/**
	 * Attempts to unregisters the {@link FileType} that matches the given name.
	 * This will fail if the {@link FileType} to unregister is the default one.
	 * <p>
	 * If the {@link FileType} to unregister is the selected {@link FileType}
	 * the new selected {@link FileType} will be the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		Validate.notNull(name, "Name cannot be null!");
		FileType fileType = get(name).orElse(null);
		if (fileType == null) return false;

		FileTypeUnregisterEvent.Before before = callEvent(new FileTypeUnregisterEvent.Before(fileType));
		if (before.isCancelled()) return false;

		boolean result = fileTypes.remove(fileType);
		if (result) callEvent(new FileTypeUnregisterEvent.After(fileType));
		return result;
	}

	/**
	 * Assigns the given extension to the {@link FileType} that matches the given name.
	 * This removes the extension from all {@link FileType}s inside this manager if the
	 * required {@link FileType} is found.
	 *
	 * @param name      the name of the {@link FileType}.
	 * @param extension the extension.
	 * @return whether the {@link FileType} was found and the extension was assigned.
	 */
	public boolean assign(String name, String extension) {
		Optional<FileType> type = get(extension);
		if (!type.isPresent()) return false;

		fileTypes.forEach(target -> target.removeExtension(extension));
		type.get().getExtensions().add(name);
		return true;
	}

	private void loadDefaults() {
		unknownType = new TextFileType("Unknown", Icons.FILE_UNKNOWN, Icons.FILE_UNKNOWN_PATH);
		folderType = new TextFileType("Folder", Icons.FILE_FOLDER, Icons.FILE_FOLDER_PATH);

		register(new TextFileType("Text", Icons.FILE_TEXT, Icons.FILE_TEXT_PATH, "txt"));
		register(new AssemblyFileType("Assembly", Icons.FILE_ASSEMBLY, Icons.FILE_ASSEMBLY_PATH, "asm", "s"));
	}

}
