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

import javafx.application.Platform;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.utils.PropertyUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents an explorer. An explorer represents graphically the list of files inside
 * its main folder.
 * <p>
 * This class can be extend to add custom functionality.
 */
public abstract class Explorer extends VBox implements ActionRegion {

	protected ScrollPane scrollPane;

	protected ExplorerSection mainSection;
	protected LinkedList<ExplorerElement> selectedElements;

	protected final boolean multiSelection;
	protected boolean keyboardSelection;

	protected Function<ExplorerBasicElement, ContextMenu> basicElementContextMenuCreator;
	protected Function<ExplorerSection, ContextMenu> sectionContextMenuCreator;

	/**
	 * Creates an explorer.
	 *
	 * @param scrollPane            the {@link ScrollPane} holding this explorer, if present.
	 * @param multiSelection        whether this explorer supports multi-selection.
	 * @param generateOnConstructor whether the method {@link #generateMainSection()} should be called on the constructor.
	 */
	public Explorer(ScrollPane scrollPane, boolean multiSelection, boolean generateOnConstructor) {
		this.scrollPane = scrollPane;
		this.multiSelection = multiSelection;
		basicElementContextMenuCreator = file -> null;
		sectionContextMenuCreator = folder -> null;

		this.selectedElements = new LinkedList<>();

		loadListeners();
		if (generateOnConstructor) {
			generateMainSection();
		}
	}

	/**
	 * Returns whether this explorer supports multiple selections.
	 * <p>
	 * If false, all selection methods will behave like {@link #setSelectedElement(ExplorerElement)}.
	 *
	 * @return whether this explorer supports multiple selections.
	 */
	public boolean supportsMultiSelection() {
		return multiSelection;
	}

	/**
	 * Returns an unmodifiable {@link List} containing all selected elements of this explorer.
	 * <p>
	 * Modifications on this {@link List} result on a {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link List}.
	 */
	public List<ExplorerElement> getSelectedElements() {
		return Collections.unmodifiableList(selectedElements);
	}

	/**
	 * Returns whether the current selection has been made by the keyboard.
	 *
	 * @return whether the current selection has been made by the keyboard.
	 */
	public boolean isKeyboardSelection() {
		return keyboardSelection;
	}

	/**
	 * Starts a mouse selection. This method should be called every time
	 * a mouse selection is created or modified.
	 */
	public void startMouseSelection() {
		if (!keyboardSelection) return;
		keyboardSelection = false;
		//Mouse selections may modify a keyboard selection.
	}

	/**
	 * Starts a keyboard selection. This method should be called every time
	 * a keyboard selection is created or modified.
	 */
	public void startKeyboardSelection() {
		if (keyboardSelection) return;
		keyboardSelection = true;
		//Keyboard selections must start with a single selected element.
		if (selectedElements.size() > 1) {
			setSelectedElement(selectedElements.getLast());
		}
	}

	/**
	 * Returns the main folder of this explorer.
	 *
	 * @return the main folder.
	 */
	public ExplorerSection getMainSection() {
		return mainSection;
	}

	/**
	 * Sets the selected element of the explorer.
	 *
	 * @param element the selected element.
	 */
	public void setSelectedElement(ExplorerElement element) {
		selectedElements.forEach(ExplorerElement::deselect);
		selectedElements.clear();

		selectedElements.add(element);
		element.select();
	}

	/**
	 * Adds or removes the given {@link ExplorerElement} from the selection.
	 * <p>
	 * If the {@link ExplorerElement} is selected, this method deselects it.
	 * Else, the method selects it.
	 * <p>
	 * If {@link #supportsMultiSelection()} is false, this method
	 * behaves like {@link #setSelectedElement(ExplorerElement)}.
	 *
	 * @param element the {@link ExplorerElement} to select or deselect.
	 */
	public void addOrRemoveSelectedElement(ExplorerElement element) {
		if (!multiSelection) {
			setSelectedElement(element);
			return;
		}
		if (selectedElements.contains(element)) {
			//REMOVE
			element.deselect();
			selectedElements.remove(element);
		} else {
			//ADD
			element.select();
			selectedElements.add(element);
		}
	}

	/**
	 * Selects all {@link ExplorerElement} between the last selected {@link ExplorerElement}
	 * and the given one.
	 * <p>
	 * Any other selected {@link ExplorerElement}s will be deselected.
	 * <p>
	 * If {@link #supportsMultiSelection()} is false, this method
	 * behaves like {@link #setSelectedElement(ExplorerElement)}.
	 *
	 * @param element the given {@link ExplorerElement}.
	 */
	public void selectTo(ExplorerElement element) {
		if (!multiSelection || selectedElements.isEmpty()) {
			setSelectedElement(element);
			return;
		}

		ExplorerElement last = selectedElements.getLast();
		if (last == element) {
			setSelectedElement(element);
			return;
		}

		selectedElements.forEach(ExplorerElement::deselect);
		selectedElements.clear();

		double lastPos = last.getExplorerYTranslation();
		double firstPos = element.getExplorerYTranslation();

		ExplorerElement current = element;

		boolean useNext = lastPos > firstPos;

		do {
			current.select();
			selectedElements.add(current);
			current = (useNext ? current.getNext() : current.getPrevious()).orElse(null);
		} while (current != null && current != last);

		last.select();
		selectedElements.add(last);
	}

	/**
	 * Manages a mouse selection from a {@link MouseEvent}.
	 * <p>
	 * If shift is down, {@link #selectTo(ExplorerElement)} is invoked.
	 * If control is down, {@link #addOrRemoveSelectedElement(ExplorerElement)} is invoked.
	 * If none of both keys are down, {@link #setSelectedElement(ExplorerElement)} is invoked.
	 *
	 * @param event   the {@link MouseEvent}.
	 * @param element the {@link ExplorerElement} to select.
	 */
	public void manageMouseSelection(MouseEvent event, ExplorerElement element) {
		if (event.isShiftDown() && event.isControlDown()) return;
		startMouseSelection();
		if (event.isShiftDown()) {
			selectTo(element);
		} else if (event.isControlDown()) {
			addOrRemoveSelectedElement(element);
		} else {
			setSelectedElement(element);
		}
	}

	/**
	 * Manages a keyboard selection from a {@link KeyEvent}.
	 * <p>
	 * If control is down, {@link #addOrRemoveSelectedElement(ExplorerElement)} is invoked.
	 * Else, {@link #setSelectedElement(ExplorerElement)} is invoked.
	 * <p>
	 * This method also updates the scroll position.
	 *
	 * @param event   the {@link KeyEvent}.
	 * @param element the {@link ExplorerElement} to select.
	 */
	public void manageKeyboardSelection(KeyEvent event, ExplorerElement element) {
		if (event.isControlDown()) return;
		startKeyboardSelection();

		if (event.isShiftDown()) {
			if (element.isSelected()) {
				addOrRemoveSelectedElement(selectedElements.getLast());
			} else {
				addOrRemoveSelectedElement(element);
			}
		} else {
			setSelectedElement(element);
		}
		updateScrollPosition(element);
	}

	/**
	 * Returns the {@link ScrollPane} holding this explorer, if present.
	 *
	 * @return the {@link ScrollPane}, if present.
	 */
	public Optional<ScrollPane> getScrollPane() {
		return Optional.ofNullable(scrollPane);
	}

	/**
	 * Returns the amount of elements inside this explorer.
	 * <p>
	 * This method also includes the element itself.
	 *
	 * @return the amount.
	 */
	public int getElementsAmount() {
		return mainSection.getTotalElements();
	}

	/**
	 * Sets the {@link Function} called to create {@link ExplorerBasicElement}'s {@link ContextMenu}s,
	 * allowing to create custom {@link ContextMenu}s when a {@link ExplorerBasicElement}
	 * is clicked using the secondary button.
	 *
	 * @param basicElementContextMenuCreator the {@link Function}.
	 */
	public void setBasicElementContextMenuCreator(Function<ExplorerBasicElement, ContextMenu> basicElementContextMenuCreator) {
		Validate.notNull(basicElementContextMenuCreator, "Function cannot be null!");
		this.basicElementContextMenuCreator = basicElementContextMenuCreator;
	}

	/**
	 * Sets the {@link Function} called to create {@link ExplorerSection}'s {@link ContextMenu}s,
	 * allowing to create custom {@link ContextMenu}s when a {@link ExplorerSection}
	 * is clicked using the secondary button.
	 *
	 * @param sectionContextMenuCreator the {@link Function}.
	 */
	public void setSectionContextMenuCreator(Function<ExplorerSection, ContextMenu> sectionContextMenuCreator) {
		Validate.notNull(sectionContextMenuCreator, "Function cannot be null!");
		this.sectionContextMenuCreator = sectionContextMenuCreator;
	}

	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerBasicElement}.
	 *
	 * @param element the {@link ExplorerBasicElement}.
	 * @return the {@link ContextMenu}.
	 * @see #setBasicElementContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerBasicElement element) {
		ContextMenu menu = basicElementContextMenuCreator.apply(element);
		return menu == null ? new ContextMenu() : menu;
	}


	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerSection}.
	 *
	 * @param section the {@link ExplorerSection}.
	 * @return the {@link ContextMenu}.
	 * @see #setSectionContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerSection section) {
		ContextMenu menu = sectionContextMenuCreator.apply(section);
		return menu == null ? new ContextMenu() : menu;
	}

	/**
	 * This method should be override to generate the main {@link ExplorerSection} of this explorer.
	 */
	protected abstract void generateMainSection();

	/**
	 * Refresh the width of the explorer.
	 * This should be used when a item is added or removed or a section is expanded or contracted.
	 */
	public void refreshWidth() {
		ObservableDoubleValue bound = PropertyUtils.getBoundValue(prefWidthProperty()).orElse(null);
		if (bound != null) {
			prefWidthProperty().unbind();
		}
		setMinWidth(10000);
		setPrefWidth(10000);
		applyCss();
		layout();

		Platform.runLater(() -> {
			double width = mainSection.getBiggestElement() + 20;
			setMinWidth(width);
			if (PropertyUtils.getBoundValue(prefWidthProperty()).isPresent()) return;
			if (bound != null) {
				prefWidthProperty().bind(bound);
			}
		});
	}

	/**
	 * Updates the scroll position of this explorer, allowing to see the
	 * given {@link ExplorerElement}.
	 *
	 * @param element the {@link ExplorerElement}.
	 */
	public void updateScrollPosition(ExplorerElement element) {
		double p = element.getExplorerYTranslation();
		double ph = element.getElementHeight();
		double ht = getHeight();
		double hv = scrollPane.getViewportBounds().getHeight();
		double vp = scrollPane.getVvalue() * (ht - hv);

		double vpn;
		if (p + 2 * ph > vp + hv) {
			vpn = p + ph - hv + 2 * ph;
		} else if (p < vp) {
			vpn = p;
		} else return;


		double sp = Math.max(0, Math.min(1, vpn / (ht - hv)));
		scrollPane.setVvalue(sp);
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return region.equals(RegionTags.EXPLORER);
	}

	private void loadListeners() {
		setOnMouseClicked(event -> {
			requestFocus();
			event.consume();
		});

		setOnKeyPressed(event -> {
			ExplorerElement element = null;
			if (event.getCode() == KeyCode.UP) {
				if (!selectedElements.isEmpty()) {
					element = selectedElements.getLast().getPrevious().orElse(null);
				}
			} else if (event.getCode() == KeyCode.DOWN) {
				if (!selectedElements.isEmpty()) {
					element = selectedElements.getLast().getNext().orElse(null);
				}
			} else return;

			if (element != null) {
				manageKeyboardSelection(event, element);
			}

			event.consume();
		});
	}
}
