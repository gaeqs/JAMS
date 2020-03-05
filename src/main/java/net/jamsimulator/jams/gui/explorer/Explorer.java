package net.jamsimulator.jams.gui.explorer;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.utils.Validate;

import java.util.function.Function;

/**
 * Represents an explorer. An explorer represents graphically the list of files inside
 * its main folder.
 * <p>
 * This class can be extend to add custom functionality.
 */
public abstract class Explorer extends VBox {

	protected ExplorerSection mainSection;
	protected ExplorerElement selectedElement;

	protected Function<ExplorerBasicElement, ContextMenu> basicElementContextMenuCreator;
	protected Function<ExplorerSection, ContextMenu> sectionContextMenuCreator;

	/**
	 * Creates an explorer.
	 */
	public Explorer(boolean generateOnConstructor) {
		basicElementContextMenuCreator = file -> null;
		sectionContextMenuCreator = folder -> null;

		loadListeners();

		if (generateOnConstructor) {
			generateMainSection();
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
		if (selectedElement == element) return;
		if (selectedElement != null)
			selectedElement.deselect();
		selectedElement = element;
		if (element != null)
			element.select();
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

	private void loadListeners() {
		setOnMouseClicked(event -> {
			requestFocus();
			event.consume();
		});

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				if (selectedElement != null) {
					selectedElement.getPrevious().ifPresent(this::setSelectedElement);
				}
				event.consume();
			} else if (event.getCode() == KeyCode.DOWN) {
				if (selectedElement != null) {
					selectedElement.getNext().ifPresent(this::setSelectedElement);
				}
				event.consume();
			}
		});
	}
}
