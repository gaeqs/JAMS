package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Optional;

/**
 * Represents a file inside an {@link Explorer}.
 */
public class ExplorerBasicElement extends HBox implements ExplorerElement {

	public static final int SPACING = 5;

	protected ExplorerSection parent;
	protected String name;

	//REPRESENTATION DATA
	protected ImageView icon;
	protected Label label;

	//HIERARCHY
	protected int hierarchyLevel;

	protected boolean selected;

	/**
	 * Creates an explorer basic element.
	 *
	 * @param parent         the {@link ExplorerSection} containing this element.
	 * @param name           the name of the element.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 */
	public ExplorerBasicElement(ExplorerSection parent, String name, int hierarchyLevel) {
		getStyleClass().add("explorer-element");
		this.parent = parent;
		this.name = name;
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
	 * Returns the {@link ExplorerSection} containing this file.
	 *
	 * @return the {@link ExplorerSection}.
	 */
	public ExplorerSection getParentSection() {
		return parent;
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
	public String getName() {
		return name;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void select() {
		if (selected) return;
		getStyleClass().add("selected-explorer-element");
		requestFocus();
		selected = true;
	}

	@Override
	public void deselect() {
		if (!selected) return;
		getStyleClass().remove("selected-explorer-element");
		setFocused(false);
		selected = false;
	}

	@Override
	public Optional<ExplorerElement> getNext() {
		int index = parent.getIndex(this);
		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
		index++;

		ExplorerSection parent = this.parent;
		Optional<ExplorerElement> element;
		do {
			element = parent.getElementByIndex(index);
			if (element.isPresent()) return element;

			if (parent.getParentSection() == null) return Optional.empty();

			index = parent.getParentSection().getIndex(parent);
			if (index == -1) {
				throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
			}
			index++;
			parent = parent.getParentSection();
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
			return Optional.of(parent);

		ExplorerElement element = parent.getElementByIndex(index).get();
		while (element instanceof ExplorerSection && ((ExplorerSection) element).isExpanded()) {

			Optional<ExplorerElement> optional = ((ExplorerSection) element).getLastChildren();
			if (!optional.isPresent()) return Optional.of(element);
			element = optional.get();

		}
		return Optional.of(element);
	}

	protected void loadElements() {
		icon = new ImageView();
		label = new Label(name);

		ExplorerSeparatorRegion separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

		getChildren().addAll(separator, icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void loadListeners() {
		setOnMouseClicked(this::onMouseClicked);

		//Only invoked when the element is focused.
		setOnKeyPressed(this::onKeyPressed);
	}

	protected void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			getExplorer().setSelectedElement(this);
			mouseEvent.consume();
		}
	}

	protected void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.LEFT) {
			getExplorer().setSelectedElement(parent);
			event.consume();
		} else if (event.getCode() == KeyCode.RIGHT) {
			getNext().ifPresent(element -> getExplorer().setSelectedElement(element));
			event.consume();
		}
	}
}
