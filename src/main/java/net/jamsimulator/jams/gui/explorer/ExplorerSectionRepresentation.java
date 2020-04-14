package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.gui.image.icon.Icons;

/**
 * This class allows {@link ExplorerSection}s to be represented inside the explorer.
 * It's functionality is similar to the class {@link ExplorerBasicElement}.
 */
public class ExplorerSectionRepresentation extends HBox {

	protected ExplorerSection section;

	protected ImageView statusIcon;
	protected ImageView icon;
	protected Label label;
	protected ExplorerSeparatorRegion separator;

	protected ExplorerSeparatorRegion emptyRegion;

	//HIERARCHY
	protected int hierarchyLevel;

	protected boolean selected;

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

		emptyRegion = new ExplorerSeparatorRegion(FileType.IMAGE_SIZE);

		loadElements();
		loadListeners();
		refreshStatusIcon();

		prefWidthProperty().bind(section.explorer.widthProperty());
	}

	public double getRepresentationWidth() {
		double statusWidth = statusIcon.getImage() == null ? 0 : statusIcon.getImage().getWidth();
		double iconWidth = icon.getImage() == null ? 0 : icon.getImage().getWidth();
		double separatorWidth = separator == null ? 0 : separator.getWidth();

		return separatorWidth + statusWidth + iconWidth + label.getWidth() + ExplorerBasicElement.SPACING * 3;
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
					Icons.EXPLORER_SECTION_EXPANDED_PATH, FileType.IMAGE_SIZE,
					FileType.IMAGE_SIZE).orElse(null);
		} else {
			icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.EXPLORER_FOLDER_COLLAPSED,
					Icons.EXPLORER_SECTION_COLLAPSED_PATH, FileType.IMAGE_SIZE,
					FileType.IMAGE_SIZE).orElse(null);
		}
		statusIcon.setImage(icon);
		if (icon == null) {
			getChildren().remove(statusIcon);
			if (!getChildren().contains(emptyRegion))
				getChildren().add(1, emptyRegion);
		} else if (!getChildren().contains(statusIcon)) {
			getChildren().remove(emptyRegion);
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
		statusIcon = new NearestImageView();
		icon = new NearestImageView();
		label = new Label(section.getName());

		separator = new ExplorerSeparatorRegion(true, hierarchyLevel);

		getChildren().addAll(separator, statusIcon, icon, label);
		setSpacing(ExplorerBasicElement.SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void loadListeners() {
		statusIcon.setOnMouseClicked(event -> {
			section.getExplorer().setSelectedElement(section);
			section.expandOrContract();
			event.consume();
		});
	}

}
