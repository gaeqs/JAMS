package net.jamsimulator.jams.gui.explorer;

import javafx.application.Platform;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.TaggedRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.utils.PropertyUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents an explorer. An explorer represents graphically the list of files inside
 * its main folder.
 * <p>
 * This class can be extend to add custom functionality.
 */
public abstract class Explorer extends VBox implements TaggedRegion {

	protected ScrollPane scrollPane;

	protected ExplorerSection mainSection;
	protected ExplorerElement selectedElement;

	protected Function<ExplorerBasicElement, ContextMenu> basicElementContextMenuCreator;
	protected Function<ExplorerSection, ContextMenu> sectionContextMenuCreator;

	/**
	 * Creates an explorer.
	 *
	 * @param scrollPane            the {@link ScrollPane} holding this explorer, if present.
	 * @param generateOnConstructor whether the method {@link #generateMainSection()} should be called on the constructor.
	 */
	public Explorer(ScrollPane scrollPane, boolean generateOnConstructor) {
		this.scrollPane = scrollPane;
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
	 * Returns the {@link ScrollPane} holding this explorer, if present.
	 *
	 * @return the {@link ScrollPane}, if present.
	 */
	public Optional<ScrollPane> getScrollPane() {
		return Optional.ofNullable(scrollPane);
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

	@Override
	public String getTag() {
		return RegionTags.EXPLORER;
	}

	private void loadListeners() {
		setOnMouseClicked(event -> {
			requestFocus();
			event.consume();
		});

		setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				if (selectedElement != null) {
					selectedElement.getPrevious().ifPresent(element -> {
						setSelectedElement(element);
						updateScrollPosition(element, true);
					});
				}
				event.consume();
			} else if (event.getCode() == KeyCode.DOWN) {
				if (selectedElement != null) {
					selectedElement.getNext().ifPresent(element -> {
						setSelectedElement(element);
						updateScrollPosition(element, false);
					});
				}
				event.consume();
			}
		});
	}

	private void updateScrollPosition(ExplorerElement element, boolean up) {
		double ty = element.getExplorerYTranslation();
		Bounds bounds = scrollPane.getViewportBounds();
		double scrollRelative = ty + bounds.getMinY();

		System.out.println(scrollPane.getVvalue());
		System.out.println(ty + "/" + getHeight());
		System.out.println(ty / getHeight());
		//If element is not visible
		if (scrollRelative < 0 || scrollRelative > bounds.getHeight()) {
			System.out.println("AAA");
			scrollPane.setVvalue(ty / getHeight());
		}
	}
}
