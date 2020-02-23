package net.jamsimulator.jams.gui.project;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;

import java.io.File;
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
	private ImageView folderStatusView;

	public ExplorePaneFolder(ExplorerPane explorer, int hierarchyLevel, File folder, ExplorePaneFolder parent) {
		super(explorer, hierarchyLevel, folder, parent);
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

		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_COLLAPSED,
				Icons.EXPLORER_FOLDER_COLLAPSED_PATH, FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE)
				.orElse(null);
		folderStatusView.setImage(icon);
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

		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_EXPANDED,
				Icons.EXPLORER_FOLDER_EXPANDED_PATH, FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE)
				.orElse(null);
		folderStatusView.setImage(icon);
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

	@Override
	protected void init() {
		super.init();

		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_EXPANDED,
				Icons.EXPLORER_FOLDER_EXPANDED_PATH, FileIconManager.IMAGE_SIZE, FileIconManager.IMAGE_SIZE)
				.orElse(null);

		folderStatusView = new ImageView(icon);
		getChildren().add(1, folderStatusView);
	}
}
