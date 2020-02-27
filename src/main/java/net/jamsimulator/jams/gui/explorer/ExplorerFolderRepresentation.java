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
public class ExplorerFolderRepresentation extends HBox {

	private ExplorerFolder folder;

	private ImageView statusIcon;
	private ImageView icon;
	private Label label;

	/**
	 * Creates the representation.
	 *
	 * @param folder the {@link ExplorerFolder} to represent.
	 */
	public ExplorerFolderRepresentation(ExplorerFolder folder) {
		this.folder = folder;
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

	private void loadElements() {
		statusIcon = new ImageView();
		icon = new ImageView(JamsApplication.getFileIconManager().getImageByFile(folder.getFolder()));
		label = new Label(folder.getFolder().getName());

		getChildren().addAll(statusIcon, icon, label);
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
			folder.getExplorer().setSelectedElement(folder);
		}
	}

}
