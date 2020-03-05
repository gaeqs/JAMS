package net.jamsimulator.jams.gui.explorer.folder;

import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFolderDefaultContextMenu;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;

/**
 * Represents an {@link Explorer} whose elements
 * are the files and directories of a given {@link File folder}.
 */
public class FolderExplorer extends Explorer {

	private File mainFolder;

	/**
	 * Creates the explorer folder.
	 *
	 * @param mainFolder the main folder of the explorer.
	 */
	public FolderExplorer(File mainFolder) {
		super(false);
		Validate.notNull(mainFolder, "Folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");
		this.mainFolder = mainFolder;

		basicElementContextMenuCreator = file -> {
			ExplorerFileDefaultContextMenu.INSTANCE.setCurrentExplorerFile((ExplorerFile) file);
			return ExplorerFileDefaultContextMenu.INSTANCE;
		};
		sectionContextMenuCreator = folder -> new ExplorerFolderDefaultContextMenu();

		generateMainSection();
	}

	public File getMainFolder() {
		return mainFolder;
	}

	/**
	 * Kills all {@link java.nio.file.WatchService}s of all folders inside this explorer.
	 * This should be used when the explorer won't be used anymore.
	 */
	public void killWatchers() {
		try {
			((ExplorerFolder) mainSection).killWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void generateMainSection() {
		this.mainSection = new ExplorerFolder(this, null, mainFolder, 0);
		getChildren().add(this.mainSection);
	}
}
