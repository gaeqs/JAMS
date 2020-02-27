package net.jamsimulator.jams.gui.explorer;

import javafx.scene.control.ContextMenu;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.explorer.context.ExplorerFolderDefaultContextMenu;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * Represents an explorer. An explorer represents graphically the list of files inside
 * its main folder.
 * <p>
 * This class can be extend to add custom functionality.
 */
public class Explorer extends VBox {

	private ExplorerFolder mainFolder;
	private ExplorerElement selectedElement;

	private Function<ExplorerFile, ContextMenu> fileContextMenuCreator;
	private Function<ExplorerFolder, ContextMenu> folderContextMenuCreator;

	/**
	 * Creates an explorer.
	 *
	 * @param mainFolder the main folder of the explorer. This folder must exist and be a directory.
	 */
	public Explorer(File mainFolder) {
		Validate.notNull(mainFolder, "Folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Folder must be a directory!");

		this.mainFolder = new ExplorerFolder(this, null, mainFolder);
		getChildren().add(this.mainFolder);


		fileContextMenuCreator = file -> {
			ExplorerFileDefaultContextMenu.INSTANCE.setCurrentExplorerFile(file);
			return ExplorerFileDefaultContextMenu.INSTANCE;
		};
		folderContextMenuCreator = folder -> new ExplorerFolderDefaultContextMenu();
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

	/**
	 * Sets the selected element of the explorer.
	 *
	 * @param element the selected element.
	 */
	public void setSelectedElement(ExplorerElement element) {
		if (selectedElement == element) return;
		if (selectedElement != null)
			selectedElement.deselect();
		selectedElement = element;
		if (element != null)
			element.select();
	}

	/**
	 * Sets the {@link Function} called to create {@link ExplorerFile}'s {@link ContextMenu}s,
	 * allowing to create custom {@link ContextMenu}s when a {@link ExplorerFile}
	 * is clicked using the secondary button.
	 *
	 * @param fileContextMenuCreator the {@link Function}.
	 */
	public void setFileContextMenuCreator(Function<ExplorerFile, ContextMenu> fileContextMenuCreator) {
		Validate.notNull(fileContextMenuCreator, "Function cannot be null!");
		this.fileContextMenuCreator = fileContextMenuCreator;
	}

	/**
	 * Sets the {@link Function} called to create {@link ExplorerFolder}'s {@link ContextMenu}s,
	 * allowing to create custom {@link ContextMenu}s when a {@link ExplorerFolder}
	 * is clicked using the secondary button.
	 *
	 * @param folderContextMenuCreator the {@link Function}.
	 */
	public void setFolderContextMenuCreator(Function<ExplorerFolder, ContextMenu> folderContextMenuCreator) {
		Validate.notNull(folderContextMenuCreator, "Function cannot be null!");
		this.folderContextMenuCreator = folderContextMenuCreator;
	}

	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerFile}.
	 *
	 * @param file the {@link ExplorerFile}.
	 * @return the {@link ContextMenu}.
	 * @see #setFileContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerFile file) {
		return fileContextMenuCreator.apply(file);
	}


	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerFolder}.
	 *
	 * @param folder the {@link ExplorerFolder}.
	 * @return the {@link ContextMenu}.
	 * @see #setFolderContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerFolder folder) {
		return folderContextMenuCreator.apply(folder);
	}
}
