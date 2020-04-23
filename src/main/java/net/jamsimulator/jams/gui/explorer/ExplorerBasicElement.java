/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.image.NearestImageView;

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
	protected ExplorerSeparatorRegion separator;

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

		prefWidthProperty().bind(parent.getExplorer().widthProperty());
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

	public double getRepresentationWidth() {
		return separator.getWidth() + icon.getFitWidth()
				+ label.getWidth() + ExplorerBasicElement.SPACING * 2;
	}

	/**
	 * Retuns the {@link Label} of this element.
	 *
	 * @return the {@link Label}.
	 */
	public Label getLabel() {
		return label;
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

	@Override
	public double getExplorerYTranslation() {
		return getLocalToParentTransform().getTy() + parent.getExplorerYTranslation();
	}

	@Override
	public int getTotalElements() {
		return 1;
	}

	protected void loadElements() {
		icon = new NearestImageView();
		label = new Label(name);

		separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

		getChildren().addAll(separator, icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void loadListeners() {
		addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);

		//Only invoked when the element is focused.
		addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
	}

	protected void onMouseClicked(MouseEvent mouseEvent) {
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			getExplorer().setSelectedElement(this);
			mouseEvent.consume();
		}
	}

	protected void onKeyPressed(KeyEvent event) {
		switch (event.getCode()) {
			case LEFT:
				getExplorer().setSelectedElement(parent);
				event.consume();
				break;
			case RIGHT:
				getNext().ifPresent(element -> getExplorer().setSelectedElement(element));
				event.consume();
				break;
			case ENTER:
				//Avoid parent to use enter.
				event.consume();
				break;
		}
	}

	@Override
	public String getTag() {
		return RegionTags.EXPLORER_ELEMENT;
	}
}
