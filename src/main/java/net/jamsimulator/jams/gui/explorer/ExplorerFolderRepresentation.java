package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;

/**
 * This class allows {@link ExplorerFolder}s to be represented inside the explorer.
 * It's functionality is similar to the class {@link ExplorerFile}.
 */
public class ExplorerFolderRepresentation extends HBox implements ExplorerElement {

	private ExplorerFolder folder;

	private ImageView statusIcon;
	private ImageView icon;
	private Label label;

	//HIERARCHY
	private int hierarchyLevel;

	private boolean selected;

	/**
	 * Creates the representation.
	 *
	 * @param folder         the {@link ExplorerFolder} to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFolderRepresentation(ExplorerFolder folder, int hierarchyLevel) {
		getStyleClass().add("explorer-element");
		this.folder = folder;
		this.hierarchyLevel = hierarchyLevel;

		selected = false;

		loadElements();
		loadListeners();
		refreshStatusIcon();
	}

	/**
	 * Refresh the status icon of the folder.
	 */
	public void refreshStatusIcon() {
		Image icon;
		if (folder.isExpanded()) {
			icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_EXPANDED,
					Icons.EXPLORER_FOLDER_EXPANDED_PATH, FileIconManager.IMAGE_SIZE,
					FileIconManager.IMAGE_SIZE).orElse(null);
		} else {
			icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_COLLAPSED,
					Icons.EXPLORER_FOLDER_COLLAPSED_PATH, FileIconManager.IMAGE_SIZE,
					FileIconManager.IMAGE_SIZE).orElse(null);
		}
		statusIcon.setImage(icon);
	}

	/**
	 * Returns the hierarchy level.
	 *
	 * @return the hierarchy level.
	 */
	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void select() {
		if (selected) return;
		getStyleClass().add("selected-explorer-element");
		selected = true;
	}

	@Override
	public void deselect() {
		if (!selected) return;
		getStyleClass().remove("selected-explorer-element");
		selected = false;
	}


	private void loadElements() {
		statusIcon = new ImageView();
		icon = new ImageView(JamsApplication.getFileIconManager().getImageByFile(folder.getFolder()));
		label = new Label(folder.getFolder().getName());

		ExplorerSeparatorRegion separator = new ExplorerSeparatorRegion(hierarchyLevel);

		getChildren().addAll(separator, statusIcon, icon, label);
		setSpacing(ExplorerFile.SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	private void loadListeners() {
		setOnMouseClicked(this::onMouseClicked);

		statusIcon.setOnMouseClicked(event -> {
			folder.expandOrContract();
			event.consume();
		});
	}


	private void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (mouseEvent.getClickCount() % 2 == 0) {
				folder.expandOrContract();
			}
			folder.getExplorer().setSelectedElement(this);
		}
	}

}
