package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;

import java.io.File;
import java.util.Optional;

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

	//HIERARCHY
	private int hierarchyLevel;

	private boolean selected;

	/**
	 * Creates an explorer file.
	 *
	 * @param parent         the {@link ExplorerFolder} containing this file.
	 * @param file           the file to represent.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerFile(ExplorerFolder parent, File file, int hierarchyLevel) {
		getStyleClass().add("explorer-element");
		this.parent = parent;
		this.file = file;
		this.hierarchyLevel = hierarchyLevel;

		selected = false;

		loadElements();
		loadListeners();

		setOnContextMenuRequested(request -> {
			getExplorer().setSelectedElement(this);
			parent.getExplorer().createContextMenu(this)
					.show(this, request.getScreenX(), request.getScreenY());
			request.consume();
		});

		parent.getExplorer().widthProperty().addListener((target, old, val) -> setPrefWidth(val.doubleValue()));
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

	@Override
	public Optional<ExplorerElement> getNext() {
		int index = parent.getIndex(this);
		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
		index++;

		ExplorerFolder parent = this.parent;
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
		int index = parent.getIndex(this);
		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
		index--;

		if (index == -1)
			return Optional.of(parent.getRepresentation());

		ExplorerElement element = parent.getElementByIndex(index).get();
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
			getExplorer().setSelectedElement(parent.getRepresentation());
		} else if (event.getCode() == KeyCode.RIGHT) {
			getNext().ifPresent(element -> getExplorer().setSelectedElement(element));
		}
	}

	private void loadElements() {
		icon = new ImageView(JamsApplication.getFileIconManager().getImageByFile(file));
		label = new Label(file.getName());

		ExplorerSeparatorRegion separator = new ExplorerSeparatorRegion(hierarchyLevel);

		getChildren().addAll(separator, icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	private void loadListeners() {
		setOnMousePressed(this::onMouseClicked);
	}

	private void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			getExplorer().setSelectedElement(this);
		}
	}
}
