package net.jamsimulator.jams.gui.project;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * Represents a file inside a {@link ExplorerPane}.
 * This file contains all interactive elements for the file, and may
 * be extensible by other classes.
 */
public class ExplorerPaneFile extends HBox {

	protected ExplorerPane explorer;

	protected int hierarchyLevel;
	protected ExplorePaneFolder parent;

	protected Image image;
	protected Label name;
	protected Region separator;

	/**
	 * Creates an {@link ExplorerPaneFile}.
	 *
	 * @param explorer       the {@link ExplorerPane} containing this file.
	 * @param hierarchyLevel the hierarchy level of this file. Used by the margin.
	 * @param image          the icon of the file.
	 * @param name           the name of the file.
	 * @param parent         the parent {@link ExplorePaneFolder} of this file, or null.
	 */
	public ExplorerPaneFile(ExplorerPane explorer, int hierarchyLevel,
							Image image, Label name, ExplorePaneFolder parent) {
		getStyleClass().add("folder-explorer-file");
		this.explorer = explorer;
		this.hierarchyLevel = hierarchyLevel;
		this.image = image;
		this.name = name;
		this.parent = parent;
		init();
	}

	/**
	 * Returns the {@link ExplorerPane} of this file.
	 *
	 * @return the {@link ExplorerPane}.
	 */
	public ExplorerPane getExplorer() {
		return explorer;
	}

	/**
	 * Returns whether the given {@link ExplorePaneFolder} is a parent of this file.
	 *
	 * @param folder the folder.
	 * @return whether it's a parent.
	 */
	public boolean isParent(ExplorePaneFolder folder) {
		return parent == folder || parent != null && parent.isParent(folder);
	}

	/**
	 * Returns the hierarchy level of this file.
	 *
	 * @return the hierarchy level.
	 */
	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	/**
	 * Sets the hierarchy level of this file.
	 *
	 * @param hierarchyLevel the hierarchy level.
	 */
	public void setHierarchyLevel(int hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
		separator.setPrefWidth(hierarchyLevel * 20);
	}

	/**
	 * Removes this file from the {@link ExplorerPane}.
	 */
	public void remove() {
		explorer.getChildren().remove(this);
	}

	private void init() {
		separator = new Region();
		separator.setPrefWidth(hierarchyLevel * 20);
		getChildren().add(separator);
		getChildren().add(new ImageView(image));
		getChildren().add(name);

	}
}
