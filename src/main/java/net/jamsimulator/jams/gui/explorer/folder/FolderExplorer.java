package net.jamsimulator.jams.gui.explorer.folder;

import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;

public class FolderExplorer extends Explorer {

	private File mainFolder;

	public FolderExplorer(File mainFolder) {
		super(false);
		Validate.notNull(mainFolder, "Folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");
		this.mainFolder = mainFolder;
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
