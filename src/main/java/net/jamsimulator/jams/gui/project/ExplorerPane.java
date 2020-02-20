package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

/**
 * Represents a file explorer.
 * A file explorer is a VBox containing all {@link ExplorerPaneFile}
 * representing the main folder's files.
 */
public class ExplorerPane extends VBox {

	private File mainFolder;

	/**
	 * Creates an explorer using the main folder.
	 *
	 * @param mainFolder the main folder.
	 * @throws NullPointerException     whether the main folder is null.
	 * @throws IllegalArgumentException whether the main folder is not a folder.
	 */
	public ExplorerPane(File mainFolder) {
		Validate.notNull(mainFolder, "Main folder cannot be null!");
		Validate.isTrue(mainFolder.isDirectory(), "Main folder is not a folder!");
		this.mainFolder = mainFolder;
		addFiles(mainFolder, 0, null);
	}

	/**
	 * Returns the main folder of this explorer.
	 *
	 * @return the main folder.
	 */
	public File getMainFolder() {
		return mainFolder;
	}

	/**
	 * Returns the index of the given file inside this explorer.
	 *
	 * @param file the given file.
	 * @return the index.
	 * @see java.util.List#indexOf(Object)
	 */
	int indexOf(ExplorerPaneFile file) {
		return getChildren().indexOf(file);
	}

	/**
	 * Adds the given folder and its children into the explorer.
	 *
	 * @param folder       the given folder.
	 * @param level        the hierarchy level. If the given folder is the main folder this parameter must be 0.
	 * @param folderParent the parent of the given folder. Null if the given folder is the main folder.
	 */
	private void addFiles(File folder, int level, ExplorePaneFolder folderParent) {
		File[] files = folder.listFiles();
		if (files == null) return;

		//Create the parent instance.
		Label folderName = new Label(folder.getName());
		ExplorePaneFolder parent = new ExplorePaneFolder(this, level, null, folderName, folderParent);
		if (folderParent != null) {
			folderParent.getFiles().add(parent);
		}
		getChildren().add(parent);

		level++;
		for (File file : files) {
			//If the file is a directory, call this method again using the folder.
			if (file.isDirectory()) {
				addFiles(file, level, parent);
			}
			//Else, add the file into the parent.
			else {
				Label fileName = new Label(file.getName());
				ExplorerPaneFile explorerFile = new ExplorerPaneFile(this, level, null, fileName, parent);
				parent.getFiles().add(explorerFile);
				getChildren().add(explorerFile);
			}
		}
	}

}
