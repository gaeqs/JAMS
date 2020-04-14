package net.jamsimulator.jams.gui.explorer.folder;

import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Represents an {@link Explorer} whose elements
 * are the files and directories of a given {@link File folder}.
 */
public class FolderExplorer extends Explorer {

	private Consumer<ExplorerFile> fileOpenAction;
	private final File mainFolder;

	/**
	 * Creates the explorer folder.
	 *
	 * @param mainFolder the main folder of the explorer.
	 */
	public FolderExplorer(File mainFolder, ScrollPane scrollPane) {
		super(scrollPane, false);
		Validate.notNull(mainFolder, "Folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");
		this.mainFolder = mainFolder;

		basicElementContextMenuCreator = file -> {
			ExplorerFileDefaultContextMenu.INSTANCE.setCurrentExplorerElement(file);
			return ExplorerFileDefaultContextMenu.INSTANCE;
		};
		sectionContextMenuCreator = folder -> {
			ExplorerFileDefaultContextMenu.INSTANCE.setCurrentExplorerElement(folder);
			return ExplorerFileDefaultContextMenu.INSTANCE;
		};

		fileOpenAction = file -> {
		};

		generateMainSection();
		refreshWidth();
	}

	/**
	 * Returns the main {@link File folder} of this explorer.
	 *
	 * @return the main {@link File folder}.
	 */
	public File getMainFolder() {
		return mainFolder;
	}

	/**
	 * Returns the {@link Consumer action} to perform when a file is double-clicked.
	 *
	 * @return the {@link Consumer action}.
	 */
	public Consumer<ExplorerFile> getFileOpenAction() {
		return fileOpenAction;
	}

	/**
	 * Sets the {@link Consumer action} to perform when a file is double-clicked.
	 *
	 * @param fileOpenAction the {@link Consumer action}.
	 */
	public void setFileOpenAction(Consumer<ExplorerFile> fileOpenAction) {
		this.fileOpenAction = fileOpenAction;
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
