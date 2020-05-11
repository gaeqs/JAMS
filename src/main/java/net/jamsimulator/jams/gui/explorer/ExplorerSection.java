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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMenuBuilder;
import net.jamsimulator.jams.gui.explorer.event.ExplorerAddElementEvent;
import net.jamsimulator.jams.gui.explorer.event.ExplorerRemoveElementEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a section inside an {@link Explorer}.
 */
public class ExplorerSection extends VBox implements ExplorerElement {

	public static final int SPACING = 1;

	protected Explorer explorer;
	protected ExplorerSection parent;

	protected String name;

	protected ExplorerSectionRepresentation representation;

	protected List<ExplorerElement> elements, filteredElements;
	protected Comparator<ExplorerElement> comparator;

	protected VBox contents;
	protected boolean expanded;

	//HIERARCHY
	protected int hierarchyLevel;

	//EVENTS
	protected EventHandler<MouseEvent> onMouseClicked;

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer       the {@link Explorer} of this section.
	 * @param parent         the {@link ExplorerSection} containing this section. This may be null.
	 * @param name           the name of the section.
	 * @param hierarchyLevel the hierarchy level, used by the spacing.
	 * @param comparator     the comparator used to sort the elements.
	 */
	public ExplorerSection(Explorer explorer, ExplorerSection parent, String name, int hierarchyLevel,
						   Comparator<ExplorerElement> comparator) {
		getStyleClass().add("explorer-section");
		this.explorer = explorer;
		this.parent = parent;
		this.name = name;
		this.hierarchyLevel = hierarchyLevel;
		this.comparator = comparator;
		this.elements = new ArrayList<>();
		this.filteredElements = new ArrayList<>(elements);

		onMouseClicked = e -> {
		};

		representation = loadRepresentation();

		contents = new VBox();
		contents.getStyleClass().add("contents");
		expanded = false;

		setSpacing(SPACING);
		contents.setSpacing(SPACING);

		loadElements();
		loadListeners();
		setOnContextMenuRequested(request -> {
			if (!representation.selected) {
				explorer.setSelectedElement(this);
			}
			createContextMenu(request.getScreenX(), request.getScreenY());
			request.consume();
		});
	}

	/**
	 * Returns the hierarchy level.
	 *
	 * @return the hierarchy level.
	 */
	public int getHierarchyLevel() {
		return hierarchyLevel;
	}

	/**
	 * Returns the {@link javafx.scene.layout.HBox} representing this section in the explorer.
	 *
	 * @return the {@link ExplorerSectionRepresentation}.
	 */
	public ExplorerSectionRepresentation getRepresentation() {
		return representation;
	}

	/**
	 * Returns whether this folder is expanded.
	 * If a explorer folder is expanded all its files will be shown on the {@link Explorer}.
	 *
	 * @return whether this folder is expanded.
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Contracts the section if this is expanded, removing all its files from the {@link Explorer} view.
	 * <p>
	 * This also contracts children explorer section.
	 */
	public void contract() {
		if (!expanded) return;
		contents.getChildren().clear();
		expanded = false;

		elements.forEach(target -> {
			if (target instanceof ExplorerSection) ((ExplorerSection) target).contract();
		});

		representation.refreshStatusIcon();
		explorer.refreshWidth();
	}

	/**
	 * Contrasts the section if this is contracted, adding all its files from the {@link Explorer} view.
	 */
	public void expand() {
		if (expanded) return;
		addAllFilesToContents();
		expanded = true;
		representation.refreshStatusIcon();
		explorer.refreshWidth();
	}

	/**
	 * Contracts or expands the section depending the whether the section is contracted or expanded.
	 *
	 * @see #isExpanded()
	 * @see #contract()
	 * @see #expand()
	 */
	public void expandOrContract() {
		if (expanded) contract();
		else expand();
	}

	/**
	 * Returns the index of the given {@link ExplorerElement} inside this explorer section.
	 *
	 * @param element the given {@link ExplorerElement}.
	 * @return the index, or -1 if not found.
	 * @see List#indexOf(Object)
	 */
	public int getIndex(Node element) {
		return contents.getChildren().indexOf(element);
	}

	/**
	 * Returns the {@link ExplorerElement} located at the given index.
	 * The {@link ExplorerSectionRepresentation} is not represented by any index.¡
	 * Use {@link #getRepresentation()} instead to get it.
	 *
	 * @param index the index.
	 * @return the element, if found.
	 */
	public Optional<ExplorerElement> getElementByIndex(int index) {
		if (index < 0 || contents.getChildren().size() <= index) return Optional.empty();

		Node node = contents.getChildren().get(index);
		if (!(node instanceof ExplorerElement)) return Optional.empty();
		return Optional.of((ExplorerElement) node);
	}

	/**
	 * Returns the first {@link ExplorerElement} of this explorer folder.
	 * <p>
	 * For this method to work the folder must be expanded!
	 *
	 * @return the first {@link ExplorerElement}.
	 */
	public Optional<ExplorerElement> getFirstChildren() {
		if (contents.getChildren().isEmpty()) return Optional.empty();
		Node node = contents.getChildren().get(0);
		if (!(node instanceof ExplorerElement)) return Optional.empty();
		return Optional.of((ExplorerElement) node);
	}

	/**
	 * Returns the last {@link ExplorerElement} of this explorer folder.
	 * <p>
	 * For this method to work the folder must be expanded!
	 *
	 * @return the first {@link ExplorerElement}.
	 */
	public Optional<ExplorerElement> getLastChildren() {
		if (contents.getChildren().isEmpty()) return Optional.empty();
		Node node = contents.getChildren().get(contents.getChildren().size() - 1);
		if (!(node instanceof ExplorerElement)) return Optional.empty();
		return Optional.of((ExplorerElement) node);
	}

	/**
	 * Returns whether this section has no {@link ExplorerElement}s.
	 *
	 * @return whether this section has no {@link ExplorerElement}s.
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * Adds the given {@link ExplorerElement} to this section.
	 *
	 * @param element the element to add.
	 */
	public void addElement(ExplorerElement element) {
		Validate.notNull(element, "Element cannot be null!");

		ExplorerAddElementEvent.Before before = new ExplorerAddElementEvent.Before(this, element);
		explorer.callEvent(before);
		if (before.isCancelled()) return;

		elements.add(element);

		if (element instanceof ExplorerBasicElement) {
			if (explorer.filter.test((ExplorerBasicElement) element)) {
				filteredElements.add(element);
			}
		} else if (element instanceof ExplorerSection) {
			if (((ExplorerSection) element).applyFilter()) {
				filteredElements.add(element);
			}
		}

		refreshAllElements();
		representation.refreshStatusIcon();
		explorer.refreshWidth();

		explorer.callEvent(new ExplorerAddElementEvent.After(this, element));
	}

	/**
	 * Removes the given {@link ExplorerElement} from this section.
	 *
	 * @param element the given element.
	 * @return whether the element was removed.
	 */
	public boolean removeElement(ExplorerElement element) {
		Validate.notNull(element, "Element cannot be null!");

		ExplorerRemoveElementEvent.Before before = new ExplorerRemoveElementEvent.Before(this, element);
		explorer.callEvent(before);
		if (before.isCancelled()) return false;

		elements.remove(element);
		boolean result = filteredElements.remove(element);
		if (result) {
			refreshAllElements();
			representation.refreshStatusIcon();
			explorer.refreshWidth();
		}

		explorer.callEvent(new ExplorerRemoveElementEvent.After(this, element));

		return result;
	}

	public void removeElementIf(Predicate<? super ExplorerElement> consumer) {
		List<ExplorerElement> filter = elements.stream().filter(consumer).collect(Collectors.toList());

		boolean result = false;
		for (ExplorerElement element : filter) {
			ExplorerRemoveElementEvent.Before before = new ExplorerRemoveElementEvent.Before(this, element);
			explorer.callEvent(before);
			if (before.isCancelled()) continue;

			elements.remove(element);
			result |= filteredElements.remove(element);

			explorer.callEvent(new ExplorerRemoveElementEvent.After(this, element));
		}

		if (result) {
			refreshAllElements();
			representation.refreshStatusIcon();
			explorer.refreshWidth();
		}
	}

	/**
	 * Returns the event handler invoked when a mouse click event is called.
	 * <p>
	 * This will not override the expansion / contraction of the section when
	 * the user double-clicked it.
	 *
	 * @return the event handler.
	 */
	public EventHandler<MouseEvent> getOnMouseClickedEvent() {
		return onMouseClicked;
	}

	/**
	 * Sets the event handler invoked when a mouse click event is called.
	 * <p>
	 * This will not override the expansion / contraction of the section when
	 * the user double-clicked it.
	 *
	 * @param eventHandler the event handler.
	 */
	public void setOnMouseClickedEvent(EventHandler<MouseEvent> eventHandler) {
		Validate.notNull(eventHandler, "Event handler cannot be null!");
		this.onMouseClicked = eventHandler;
	}

	/**
	 * Returns the width property of the biggest element in this section.
	 * This may return the {@link ExplorerSectionRepresentation} of this section.
	 *
	 * @return the width property.
	 */
	public double getBiggestElement() {
		double property = getRepresentation().getRepresentationWidth();
		if (!isExpanded()) return property;

		double current;
		for (ExplorerElement element : elements) {
			if (element instanceof ExplorerSection) {
				current = ((ExplorerSection) element).getBiggestElement();
			} else if (element instanceof ExplorerBasicElement) {
				current = ((ExplorerBasicElement) element).getRepresentationWidth();
			} else continue;
			if (property < current) {
				property = current;
			}
		}
		return property;
	}

	protected boolean applyFilter() {
		filteredElements.clear();
		for (ExplorerElement element : elements) {
			if (element instanceof ExplorerBasicElement) {
				if (explorer.filter.test((ExplorerBasicElement) element)) {
					filteredElements.add(element);
				}
			} else if (element instanceof ExplorerSection) {
				if (((ExplorerSection) element).applyFilter()) {
					filteredElements.add(element);
				}
			}
		}


		if (isExpanded()) {
			refreshAllElements();
		}

		return elements.isEmpty() || !filteredElements.isEmpty();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVisibleName() {
		return name;
	}

	@Override
	public Explorer getExplorer() {
		return explorer;
	}

	@Override
	public Optional<? extends ExplorerSection> getParentSection() {
		return Optional.ofNullable(parent);
	}

	@Override
	public boolean isSelected() {
		return representation.isSelected();
	}

	@Override
	public void select() {
		representation.select();
		requestFocus();
	}

	@Override
	public void deselect() {
		representation.deselect();
	}

	@Override
	public Optional<ExplorerElement> getNext() {
		Optional<ExplorerElement> optional = getFirstChildren();
		if (optional.isPresent()) return optional;

		ExplorerSection parent = this.parent;
		if (parent == null) return Optional.empty();
		int index = parent.getIndex(this);

		if (index == -1)
			throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");

		index++;

		Optional<ExplorerElement> element;
		do {
			element = parent.getElementByIndex(index);
			if (element.isPresent()) return element;

			if (!parent.getParentSection().isPresent()) return Optional.empty();

			index = parent.getParentSection().get().getIndex(parent);
			if (index == -1) {
				throw new IllegalStateException("Error while getting the next element. File is not inside the folder.");
			}
			index++;
			parent = parent.getParentSection().orElse(null);
		} while (parent != null);
		return element;
	}

	@Override
	public Optional<ExplorerElement> getPrevious() {
		if (parent == null) return Optional.empty();
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
		return parent == null ? getLocalToParentTransform().getTy() : getLocalToParentTransform().getTy() + parent.getExplorerYTranslation();
	}

	@Override
	public double getElementHeight() {
		return representation.getHeight();
	}

	@Override
	public int getTotalElements() {
		return 1 + elements.stream().mapToInt(ExplorerElement::getTotalElements).sum();
	}

	@Override
	public void createContextMenu(double screenX, double screenY) {
		Set<ContextAction> set = getSupportedContextActions();
		if (set.isEmpty()) return;
		ContextMenu main = new ContextActionMenuBuilder(this).addAll(set).build();
		JamsApplication.openContextMenu(main, this, screenX, screenY);
	}

	private Set<ContextAction> getSupportedContextActions() {
		Explorer explorer = getExplorer();
		Set<Action> actions = JamsApplication.getActionManager().getAll();
		Set<ContextAction> set = new HashSet<>();
		for (Action action : actions) {
			if (action instanceof ContextAction && supportsActionRegion(action.getRegionTag())
					&& ((ContextAction) action).supportsExplorerState(explorer)) {
				set.add((ContextAction) action);
			}
		}
		return set;
	}

	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionRepresentation(this, hierarchyLevel);
	}

	protected void loadElements() {
		getChildren().clear();
		getChildren().add(representation);
		getChildren().add(contents);
	}

	protected void loadListeners() {
		addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);

		//Only invoked when the element is focused.
		addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
	}

	protected void refreshAllElements() {
		//Clears, sorts and adds the files.
		contents.getChildren().clear();
		filteredElements.sort(comparator);
		if (expanded) addAllFilesToContents();
	}

	protected void addAllFilesToContents() {
		filteredElements.forEach(target -> {
			if (target instanceof Node)
				contents.getChildren().add((Node) target);
		});
	}

	protected void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			expandOrContract();
			event.consume();
		}
	}

	protected void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (!mouseEvent.isControlDown() && !mouseEvent.isShiftDown() && mouseEvent.getClickCount() % 2 == 0) {
				expandOrContract();
			}
			explorer.manageMouseSelection(mouseEvent, this);
			onMouseClicked.handle(mouseEvent);
			mouseEvent.consume();
		}
	}

	protected void selectAll() {
		explorer.addOrRemoveSelectedElement(this);
		for (ExplorerElement element : filteredElements) {
			if (element instanceof ExplorerSection) {
				((ExplorerSection) element).selectAll();
			} else {
				explorer.addOrRemoveSelectedElement(element);
			}
		}
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return region.equals(RegionTags.EXPLORER_ELEMENT);
	}
}
