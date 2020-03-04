package net.jamsimulator.jams.gui.explorer;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFileDefaultContextMenu;
import net.jamsimulator.jams.gui.explorer.folder.context.ExplorerFolderDefaultContextMenu;
import net.jamsimulator.jams.utils.Validate;

import java.io.IOException;
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

	protected Function<ExplorerBasicElement, ContextMenu> fileContextMenuCreator;
	protected Function<ExplorerSection, ContextMenu> folderContextMenuCreator;

	/**
	 * Creates an explorer.
	 */
	public Explorer(boolean generateOnConstructor) {
		fileContextMenuCreator = file -> {
			ExplorerFileDefaultContextMenu.INSTANCE.setCurrentExplorerFile((ExplorerFile) file);
			return ExplorerFileDefaultContextMenu.INSTANCE;
		};
		folderContextMenuCreator = folder -> new ExplorerFolderDefaultContextMenu();

		setOnMouseClicked(event -> {
			requestFocus();
			event.consume();
		});

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				if (selectedElement != null) {
					selectedElement.getPrevious().ifPresent(this::setSelectedElement);
				}
			} else if (event.getCode() == KeyCode.DOWN) {
				if (selectedElement != null) {
					selectedElement.getNext().ifPresent(this::setSelectedElement);
				}
			} else {
				if (selectedElement != null) {
					selectedElement.handleKeyPressEvent(event);
				}
			}
			event.consume();
		});

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
	 * @param fileContextMenuCreator the {@link Function}.
	 */
	public void setFileContextMenuCreator(Function<ExplorerBasicElement, ContextMenu> fileContextMenuCreator) {
		Validate.notNull(fileContextMenuCreator, "Function cannot be null!");
		this.fileContextMenuCreator = fileContextMenuCreator;
	}

	/**
	 * Sets the {@link Function} called to create {@link ExplorerSection}'s {@link ContextMenu}s,
	 * allowing to create custom {@link ContextMenu}s when a {@link ExplorerSection}
	 * is clicked using the secondary button.
	 *
	 * @param folderContextMenuCreator the {@link Function}.
	 */
	public void setFolderContextMenuCreator(Function<ExplorerSection, ContextMenu> folderContextMenuCreator) {
		Validate.notNull(folderContextMenuCreator, "Function cannot be null!");
		this.folderContextMenuCreator = folderContextMenuCreator;
	}

	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerBasicElement}.
	 *
	 * @param file the {@link ExplorerBasicElement}.
	 * @return the {@link ContextMenu}.
	 * @see #setFileContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerBasicElement file) {
		return fileContextMenuCreator.apply(file);
	}


	/**
	 * Creates a {@link ContextMenu} for the given {@link ExplorerSection}.
	 *
	 * @param folder the {@link ExplorerSection}.
	 * @return the {@link ContextMenu}.
	 * @see #setFolderContextMenuCreator(Function)
	 */
	public ContextMenu createContextMenu(ExplorerSection folder) {
		return folderContextMenuCreator.apply(folder);
	}

	protected abstract void generateMainSection();
}
