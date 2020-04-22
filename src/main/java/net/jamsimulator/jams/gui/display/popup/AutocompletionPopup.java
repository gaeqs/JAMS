package net.jamsimulator.jams.gui.display.popup;

import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.CodeFileDisplay;
import net.jamsimulator.jams.utils.CharacterCodes;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is a small guide to implement autocompletion popups.
 * <p>
 * It contains the default style and controls.
 */
public abstract class AutocompletionPopup extends Popup {

	protected final CodeFileDisplay display;
	protected final VBox content;
	protected final List<AutocompletionPopupElement> elements;

	protected AutocompletionPopupElement selected;
	protected int selectedIndex;

	protected ScrollPane scroll;


	/**
	 * Creates the autocompletion popup.
	 *
	 * @param display the code display where this popup is displayed.
	 */
	public AutocompletionPopup(CodeFileDisplay display) {
		this.display = display;
		content = new VBox();
		content.getStyleClass().add("autocompletion-popup");
		elements = new ArrayList<>();


		scroll = new ScrollPane(content);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		getContent().add(scroll);
		scroll.setMaxHeight(200);
	}

	/**
	 * Returns the {@link CodeFileDisplay} where this popup is displayed.
	 *
	 * @return the {@link CodeFileDisplay}.
	 */
	public CodeFileDisplay getDisplay() {
		return display;
	}

	/**
	 * Returns whether this popup has no contents.
	 * <p>
	 * {@link #refreshContents(int)} should be executed before this.
	 *
	 * @return whether this popup has no contents.
	 */
	public boolean isEmpty() {
		return content.getChildren().isEmpty();
	}

	/**
	 * Returns the amount of elements inside this popup.
	 * <p>
	 * {@link #refreshContents(int)} should be executed before this.
	 *
	 * @return the amount of elements inside this popup.
	 */
	public int size() {
		return content.getChildren().size();
	}


	/**
	 * Selects the element above the current selected element.
	 */
	public void moveUp() {
		selectedIndex--;
		if (selectedIndex < 0) {
			selectedIndex = content.getChildren().size() - 1;
			scroll.setVvalue(1);
			refreshSelected();
		} else {
			refreshSelected();
			updateScrollPosition();
		}

	}

	/**
	 * Selects the element below the current selected element.
	 */
	public void moveDown() {
		selectedIndex++;
		if (selectedIndex == content.getChildren().size()) {
			selectedIndex = 0;
			scroll.setVvalue(0);
			refreshSelected();
		} else {
			refreshSelected();
			updateScrollPosition();
		}
	}

	/**
	 * Adds the given elements to this popup.
	 *
	 * @param collection               the elements.
	 * @param conversion               the function that converts the element into a {@link String}.
	 * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
	 * @param <T>                      the type of the elements.
	 */
	public <T> void addElements(Collection<T> collection, Function<T, String> conversion,
								Function<T, String> autocompletionConversion) {
		addElements(collection.iterator(), conversion, autocompletionConversion);
	}

	/**
	 * Adds the given elements to this popup.
	 *
	 * @param collection               the elements.
	 * @param conversion               the function that converts the element into a {@link String}.
	 * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
	 * @param <T>                      the type of the elements.
	 */
	public <T> void addElements(Stream<T> collection, Function<T, String> conversion,
								Function<T, String> autocompletionConversion) {
		addElements(collection.iterator(), conversion, autocompletionConversion);
	}

	/**
	 * Adds the given elements to this popup.
	 *
	 * @param iterator                 the elements.
	 * @param conversion               the function that converts the element into a {@link String}.
	 * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
	 * @param <T>                      the type of the elements.
	 */
	public <T> void addElements(Iterator<T> iterator, Function<T, String> conversion,
								Function<T, String> autocompletionConversion) {
		AutocompletionPopupElement label;
		T next;
		while (iterator.hasNext()) {
			next = iterator.next();
			label = new AutocompletionPopupElement(StringUtils.addExtraSpaces(conversion.apply(next)),
					autocompletionConversion.apply(next));
			elements.add(label);
		}
	}

	/**
	 * Refreshes the selected element. Used by {@link #moveUp()} and {@link #moveDown()}.
	 */
	protected void refreshSelected() {
		if (selected != null) {
			selected.getStyleClass().remove("autocompletion-popup-element-selected");
		}
		selected = (AutocompletionPopupElement) content.getChildren().get(selectedIndex);
		selected.getStyleClass().add("autocompletion-popup-element-selected");
	}


	protected void updateScrollPosition() {
		if (selected == null) return;
		Bounds bounds = scroll.getViewportBounds();

		double scrollRelative = selected.getLocalToParentTransform().getTy() + bounds.getMinY();


		//If element is not visible
		double height = getHeight();
		if (scrollRelative < 40) {
			scroll.setVvalue(scroll.getVvalue() + (scrollRelative - 40) / height);
		}
		if (scrollRelative > bounds.getHeight() - 80) {
			scroll.setVvalue(scroll.getVvalue() + (scrollRelative - bounds.getHeight() + 80) / height);
		}
	}


	//region EVENTS

	/**
	 * Manages a {@link CodeFileDisplay}'s press event.
	 *
	 * @param event the event.
	 */
	public void managePressEvent(KeyEvent event) {
		if (event.isControlDown() || event.isAltDown() || event.isMetaDown() || event.isShortcutDown()) return;

		byte b = event.getCharacter().getBytes()[0];

		if (b == CharacterCodes.ENTER) return;
		if (b == CharacterCodes.ESCAPE || b == CharacterCodes.SPACE) hide();
		else {
			if (!isShowing() && b == CharacterCodes.BACKSPACE) return;
			execute(0, false);
		}
	}

	public void sortAndShowElements(String hint) {
		content.getChildren().clear();
		elements.sort((o1, o2) -> {
			if (o1.getName().equals(hint)) return -1;
			if (o2.getName().equals(hint)) return 1;

			return o1.getName().compareTo(o2.getName());
		});
		content.getChildren().addAll(elements);
	}

	/**
	 * Manages a {@link CodeFileDisplay}'s press event. This should be called on the filter stage.
	 *
	 * @param event the event.
	 * @return whether the event should be cancelled.
	 */
	public boolean manageTypeEvent(KeyEvent event) {
		if (!isShowing()) return false;
		if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
				|| event.getCode() == KeyCode.BACK_SPACE) {
			execute(event.getCode() == KeyCode.RIGHT ? 1 : -1, false);
			return false;
		}
		if (event.getCode() == KeyCode.UP) {
			moveUp();
			return true;
		}
		if (event.getCode() == KeyCode.DOWN) {
			moveDown();
			return true;
		}
		if (event.getCode() == KeyCode.ENTER) {
			autocomplete();
			hide();
			return true;
		}

		return false;
	}

	//endregion

	/**
	 * Tries to open the popup at the caret position, refreshing it.
	 * The caret position can be modified using the parameter 'caretOffset'.
	 * <p>
	 * If 'autocompleteIfOne' is true and there's only one element inside the popup the popup won't open
	 * and it will call the method {@link #autocomplete()}.
	 *
	 * @param caretOffset       the caret offset.
	 * @param autocompleteIfOne whether it should autocomplete instead of opening of there's only one element.
	 */
	public abstract void execute(int caretOffset, boolean autocompleteIfOne);

	/**
	 * Refreshes the contents inside this popup.
	 *
	 * @param caretPosition the caret position.
	 */
	public abstract void refreshContents(int caretPosition);

	/**
	 * Autocompletes using the selected element.
	 */
	public abstract void autocomplete();


}
