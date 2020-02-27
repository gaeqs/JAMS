package net.jamsimulator.jams.gui.explorer;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;

/**
 * Represents an explorer. An explorer represents graphically the list of files inside
 * its main folder.
 * <p>
 * This class can be extend to add custom functionality.
 */
public class Explorer extends VBox {

	private ExplorerFolder mainFolder;

	/**
	 * Creates an explorer.
	 *
	 * @param mainFolder the main folder of the explorer. This folder must exist and be a directory.
	 */
	public Explorer(File mainFolder) {
		Validate.notNull(mainFolder, "Folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");

		this.mainFolder = new ExplorerFolder(mainFolder);
		getChildren().add(this.mainFolder);
	}

	/**
	 * Kills all {@link java.nio.file.WatchService}s of all folders inside this explorer.
	 * This should be used when the explorer won't be used anymore.
	 */
	public void killWatchers() {
		try {
			this.mainFolder.killWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main folder of this explorer.
	 *
	 * @return the main folder.
	 */
	public ExplorerFolder getMainFolder() {
		return mainFolder;
	}
}
