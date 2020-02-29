package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.icon.FileIconManager;
import net.jamsimulator.jams.gui.icon.Icons;

import java.util.Optional;

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
	 * Returns the represented {@link ExplorerFolder}.
	 *
	 * @return the represented {@link ExplorerFolder}.
	 */
	public ExplorerFolder getFolder() {
		return folder;
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

	@Override
	public Optional<ExplorerElement> getNext() {
		Optional<ExplorerElement> optional = folder.getFirstChildren();
		if (optional.isPresent()) return optional;

		ExplorerFolder parent = folder.getParentFolder();

		if (parent == null) return Optional.empty();

		int index = folder.getParentFolder().getIndex(folder);

		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");

		index++;

		Optional<ExplorerElement> element;
		do {
			element = parent.getElementByIndex(index);
			if (element.isPresent()) return element;

			if (parent.getParentFolder() == null) return Optional.empty();

			index = parent.getParentFolder().getIndex(parent);
			if (index == -1) {
				throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
			}
			index++;
			parent = parent.getParentFolder();
		} while (parent != null);
		return element;
	}

	@Override
	public Optional<ExplorerElement> getPrevious() {
		if (folder.getParentFolder() == null) return Optional.empty();
		int index = folder.getParentFolder().getIndex(folder);
		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
		index--;

		if (index == -1)
			return Optional.of(folder.getParentFolder().getRepresentation());

		ExplorerElement element = folder.getParentFolder().getElementByIndex(index).get();
		while (element instanceof ExplorerFolderRepresentation && ((ExplorerFolderRepresentation) element).getFolder().isExpanded()) {

			Optional<ExplorerElement> optional = ((ExplorerFolderRepresentation) element).getFolder().getLastChildren();
			if (!optional.isPresent()) return Optional.of(element);
			element = optional.get();

		}
		return Optional.of(element);

	}

	@Override
	public void handleKeyPressEvent(KeyEvent event) {
		if (event.getCode() == KeyCode.LEFT) {
			if (folder.isExpanded()) {
				folder.contract();
			} else {
				getPrevious().ifPresent(element -> folder.getExplorer().setSelectedElement(element));
			}
		} else if (event.getCode() == KeyCode.RIGHT) {
			if (!folder.isExpanded()) {
				folder.expand();
			} else {
				getNext().ifPresent(element -> folder.getExplorer().setSelectedElement(element));
			}
		}
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
