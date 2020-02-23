package net.jamsimulator.jams.gui.project;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.JamsApplication;

import java.io.File;

/**
 * Represents a file inside a {@link ExplorerPane}.
 * This file contains all interactive elements for the file, and may
 * be extensible by other classes.
 */
public class ExplorerPaneFile extends HBox {

	public static final int SPACING = 5;

	protected ExplorerPane explorer;
	protected File file;

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
	 * @param file           the file.
	 * @param parent         the parent {@link ExplorePaneFolder} of this file, or null.
	 */
	public ExplorerPaneFile(ExplorerPane explorer, int hierarchyLevel, File file, ExplorePaneFolder parent) {
		getStyleClass().add("folder-explorer-file");
		this.explorer = explorer;
		this.file = file;
		this.hierarchyLevel = hierarchyLevel;
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
	 * Returns the handled {@link File}.
	 *
	 * @return the handled {@link File}.
	 */
	public File getFile() {
		return file;
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

		image = JamsApplication.getFileIconManager().getImageByFile(file);
		name = new Label(file.getName());

		separator = new Region();
		separator.setPrefWidth(hierarchyLevel * 20);
		getChildren().add(separator);
		getChildren().add(new ImageView(image));
		getChildren().add(name);
		setSpacing(5);
		setAlignment(Pos.CENTER_LEFT);
	}
}
