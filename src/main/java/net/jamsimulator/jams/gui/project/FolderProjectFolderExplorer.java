package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.File;

public class FolderProjectFolderExplorer extends VBox {

	private File mainFolder;

	public FolderProjectFolderExplorer(File mainFolder) {
		this.mainFolder = mainFolder;

		File[] files = mainFolder.listFiles();
		if (files == null) return;

		for (File file : files) {
			getChildren().add(new Label(file.getName()));
		}

	}

}
