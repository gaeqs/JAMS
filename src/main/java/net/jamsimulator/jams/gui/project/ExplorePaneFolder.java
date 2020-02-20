package net.jamsimulator.jams.gui.project;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a folder inside a {@link ExplorerPane}.
 * This class extends {@link ExplorerPaneFile}, and contains all its parameters.
 * <p>
 * A folder also contains a list of {@link ExplorerPaneFile} containing all its children
 * and a boolean parameter representing whether this folder is expanded.
 * <p>
 * If a folder is not expanded all its files are not present in the {@link ExplorerPane}.
 */
public class ExplorePaneFolder extends ExplorerPaneFile {

	private List<ExplorerPaneFile> files;
	private boolean expanded;

	public ExplorePaneFolder(ExplorerPane explorer, int hierarchyLevel, Image image,
							 Label name, ExplorePaneFolder parent) {
		super(explorer, hierarchyLevel, image, name, parent);
		files = new LinkedList<>();
		expanded = true;


		setOnMouseClicked(this::onMouseClicked);
	}

	/**
	 * Returns a mutable list containing the children of this folder.
	 *
	 * @return the list.
	 */
	public List<ExplorerPaneFile> getFiles() {
		return files;
	}

	/**
	 * Returns whether this folder is expanded.
	 *
	 * @return whether this folder is expanded.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Contracts this folder, removing all its children from the {@link ExplorerPane}.
	 */
	public void contract() {
		if (!expanded) return;
		files.forEach(ExplorerPaneFile::remove);
		expanded = false;
	}

	/**
	 * Expands this folder, adding all its children into the {@link ExplorerPane}.
	 */
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
		files.forEach(ExplorerPaneFile::remove);
		expanded = false;
	}

	private void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (mouseEvent.getClickCount() % 2 == 0) {
				if (expanded) contract();
				else expand();
			}
		}
	}
}
