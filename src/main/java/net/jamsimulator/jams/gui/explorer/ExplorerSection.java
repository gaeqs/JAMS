package net.jamsimulator.jams.gui.explorer;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.utils.Validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Represents a section inside an {@link Explorer}.
 */
public class ExplorerSection extends VBox implements ExplorerElement {

	public static final int SPACING = 1;

	protected Explorer explorer;
	protected ExplorerSection parent;

	protected String name;

	protected ExplorerSectionRepresentation representation;

	protected List<ExplorerElement> elements;
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
		this.explorer = explorer;
		this.parent = parent;
		this.name = name;
		this.hierarchyLevel = hierarchyLevel;
		this.comparator = comparator;
		this.elements = new ArrayList<>();

		onMouseClicked = e -> {
		};

		representation = loadRepresentation();

		contents = new VBox();
		expanded = false;

		setSpacing(SPACING);
		contents.setSpacing(SPACING);

		loadElements();
		loadListeners();
		setOnContextMenuRequested(request -> {
			explorer.setSelectedElement(this);
			explorer.createContextMenu(this).
					show(this, request.getScreenX(), request.getScreenY());
			request.consume();
		});

		explorer.widthProperty().addListener((target, old, val) -> setPrefWidth(val.doubleValue()));
	}

	/**
	 * Returns the {@link Explorer} of this section.
	 *
	 * @return the {@link Explorer}.
	 */
	public Explorer getExplorer() {
		return explorer;
	}

	/**
	 * Returns the {@link ExplorerSection} containing this section.
	 * This value is null when this section is the root section.
	 *
	 * @return the {@link ExplorerSection}.
	 */
	public ExplorerSection getParentSection() {
		return parent;
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
	}

	/**
	 * Contrasts the section if this is contracted, adding all its files from the {@link Explorer} view.
	 */
	public void expand() {
		if (expanded) return;
		addAllFilesToContents();
		expanded = true;
		representation.refreshStatusIcon();
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
		elements.add(element);
		refreshAllElements();
		representation.refreshStatusIcon();
	}

	/**
	 * Removes the given {@link ExplorerElement} from this section.
	 *
	 * @param element the given element.
	 * @return whether the element was removed.
	 */
	public boolean removeElement(ExplorerElement element) {
		Validate.notNull(element, "Element cannot be null!");
		boolean result = elements.remove(element);
		if (result) {
			refreshAllElements();
			representation.refreshStatusIcon();
		}
		return result;
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

	@Override
	public String getName() {
		return name;
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

	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionRepresentation(this, hierarchyLevel);
	}

	protected void loadElements() {
		getChildren().clear();
		getChildren().add(representation);
		getChildren().add(contents);
	}

	protected void loadListeners() {
		setOnMouseClicked(this::onMouseClicked);
		//Only invoked when the element is focused.
		setOnKeyPressed(this::onKeyPressed);
	}

	protected void refreshAllElements() {
		//Clears, sorts and adds the files.
		contents.getChildren().clear();
		elements.sort(comparator);
		if (expanded) addAllFilesToContents();
	}

	protected void addAllFilesToContents() {
		elements.forEach(target -> {
			if (target instanceof Node)
				contents.getChildren().add((Node) target);
		});
	}

	protected void onKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.LEFT) {
			if (expanded) {
				contract();
			} else {
				getPrevious().ifPresent(element -> explorer.setSelectedElement(element));
			}
			event.consume();
		} else if (event.getCode() == KeyCode.RIGHT) {
			if (!expanded) {
				expand();
			} else {
				getNext().ifPresent(element -> explorer.setSelectedElement(element));
			}
			event.consume();
		}
	}

	protected void onMouseClicked(MouseEvent mouseEvent) {
		//Folders require a double click to expand or contract itself.
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			if (mouseEvent.getClickCount() % 2 == 0) {
				expandOrContract();
			}
			explorer.setSelectedElement(this);
			onMouseClicked.handle(mouseEvent);
			mouseEvent.consume();
		}
	}

	@Override
	public String getTag() {
		return RegionTags.EXPLORER_ELEMENT;
	}
}
