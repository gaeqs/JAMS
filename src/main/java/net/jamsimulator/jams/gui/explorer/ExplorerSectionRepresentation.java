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
 * This class allows {@link ExplorerSection}s to be represented inside the explorer.
 * It's functionality is similar to the class {@link ExplorerBasicElement}.
 */
public class ExplorerSectionRepresentation extends HBox {

	protected ExplorerSection section;

	protected ImageView statusIcon;
	protected ImageView icon;
	protected Label label;

	//HIERARCHY
	private int hierarchyLevel;

	private boolean selected;

	/**
	 * Creates the representation.
	 *
	 * @param section        the {@link ExplorerSection} to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerSectionRepresentation(ExplorerSection section, int hierarchyLevel) {
		getStyleClass().add("explorer-element");
		this.section = section;
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
		if (section.isEmpty()) {
			icon = null;
		} else if (section.isExpanded()) {
			icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_EXPANDED,
					Icons.EXPLORER_SECTION_EXPANDED_PATH, FileIconManager.IMAGE_SIZE,
					FileIconManager.IMAGE_SIZE).orElse(null);
		} else {
			icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_COLLAPSED,
					Icons.EXPLORER_SECTION_COLLAPSED_PATH, FileIconManager.IMAGE_SIZE,
					FileIconManager.IMAGE_SIZE).orElse(null);
		}
		statusIcon.setImage(icon);
		if (icon == null) {
			getChildren().remove(statusIcon);
		} else if (!getChildren().contains(statusIcon)) {
			getChildren().add(1, statusIcon);
		}
	}


	/**
	 * Returns the represented {@link ExplorerSection}.
	 *
	 * @return the represented {@link ExplorerSection}.
	 */
	public ExplorerSection getSection() {
		return section;
	}

	/**
	 * Returns the hierarchy level.
	 *
	 * @return the hierarchy level.
	 */
	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	public boolean isSelected() {
		return selected;
	}

	public void select() {
		if (selected) return;
		getStyleClass().add("selected-explorer-element");
		selected = true;
	}

	public void deselect() {
		if (!selected) return;
		getStyleClass().remove("selected-explorer-element");
		selected = false;
	}


	protected void loadElements() {
		statusIcon = new ImageView();
		icon = new ImageView();
		label = new Label(section.getName());

		ExplorerSeparatorRegion separator = new ExplorerSeparatorRegion(hierarchyLevel);

		getChildren().addAll(separator, statusIcon, icon, label);
		setSpacing(ExplorerBasicElement.SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void loadListeners() {
		setOnMousePressed(this::onMouseClicked);

		statusIcon.setOnMouseClicked(event -> {
			section.expandOrContract();
			event.consume();
		});
	}


	protected void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (mouseEvent.getClickCount() % 2 == 0) {
				section.expandOrContract();
			}
			section.getExplorer().setSelectedElement(section);
			mouseEvent.consume();
		}
	}

}
