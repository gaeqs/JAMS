package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;

import java.io.File;

/**
 * Represents a file inside an {@link Explorer}.
 */
public class ExplorerFile extends HBox implements ExplorerElement {

	public static final int SPACING = 5;

	private ExplorerFolder parent;
	private File file;

	//REPRESENTATION DATA
	private ImageView icon;
	private Label label;

	private boolean selected;

	/**
	 * Creates an explorer file.
	 *
	 * @param parent the {@link ExplorerFolder} containing this file.
	 * @param file   the file to represent.
	 */
	public ExplorerFile(ExplorerFolder parent, File file) {
		getStyleClass().add("explorer-element");
		this.parent = parent;
		this.file = file;

		selected = false;

		loadElements();
		setOnContextMenuRequested(request -> {
			parent.getExplorer().createContextMenu(this)
					.show(this, request.getScreenX(), request.getScreenY());
			request.consume();
		});
	}

	/**
	 * Returns the {@link ExplorerFolder} containing this file.
	 *
	 * @return the {@link ExplorerFolder}.
	 */
	public ExplorerFolder getParentFolder() {
		return parent;
	}

	/**
	 * Returns the {@link File} represented by this explorer file.
	 *
	 * @return the {@link File}.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the {@link Explorer} of this file.
	 *
	 * @return the {@link Explorer}.
	 */
	public Explorer getExplorer() {
		return parent.getExplorer();
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
		icon = new ImageView(JamsApplication.getFileIconManager().getImageByFile(file));
		label = new Label(file.getName());

		getChildren().addAll(icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}
}
