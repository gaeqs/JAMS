package net.jamsimulator.jams.gui.project;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

import java.util.LinkedList;
import java.util.List;

public class FolderExplorerFolder extends FolderExplorerFile {

	private List<FolderExplorerFile> files;
	private boolean expanded;

	public FolderExplorerFolder(FolderProjectFolderExplorer explorer, int hierarchyLevel, Image image,
								Label name, FolderExplorerFolder parent) {
		super(explorer, hierarchyLevel, image, name, parent);
		files = new LinkedList<>();
		expanded = true;


		setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
				if (mouseEvent.getClickCount() % 2 == 0) {
					if (expanded) contract();
					else expand();
				}
			}
		});
	}

	public List<FolderExplorerFile> getFiles() {
		return files;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void contract() {
		if (!expanded) return;
		files.forEach(FolderExplorerFile::remove);
		expanded = false;
	}

	public void expand() {
		if (expanded) return;
		ObservableList<Node> children = explorer.getChildren();

		int index = explorer.indexOf(this);
		children.addAll(index + 1, files);

		expanded = true;
	}

	@Override
	public void remove() {
		super.remove();
		files.forEach(FolderExplorerFile::remove);
		expanded = false;
	}
}
