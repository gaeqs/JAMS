package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.language.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a element inside a MIPS file.
 */
public abstract class MipsCodeElement {

	protected int startIndex;
	protected int endIndex;
	protected final String text;

	protected List<MipsDisplayError> errors;

	/**
	 * Creates the element.
	 * <p>
	 * The start and end indices must be file absolute indices.
	 *
	 * @param startIndex the start index.
	 * @param endIndex   the end index.
	 * @param text       the text.
	 */
	public MipsCodeElement(int startIndex, int endIndex, String text) {
		if (startIndex > endIndex) {
			throw new IllegalArgumentException("Start index (" + startIndex + ") is bigger than the end index (" + endIndex + ").");
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.text = text;
		this.errors = new ArrayList<>();
	}

	/**
	 * Returns the file absolute index where this element starts. This index is inclusive.
	 *
	 * @return the index.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Returns the file absolute index where this element ends. This index is exclusive.
	 *
	 * @return the index.
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * Moves the element.
	 *
	 * @param offset the chars the element must move.
	 */
	public void move(int offset) {
		startIndex += offset;
		endIndex += offset;
	}

	/**
	 * Returns the text representing this element.
	 *
	 * @return the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns a unmodifiable {@link List} with all errors inside this element.
	 * The method {@link #searchErrors(WorkingPane, MipsFileElements)} must be invoked before use this method.
	 *
	 * @return the {@link List}.
	 */
	public List<MipsDisplayError> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	/**
	 * Returns whether this element has any {@link MipsDisplayError}.
	 *
	 * @return whether this element has any {@link MipsDisplayError}.
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Returns the styles to apply to this element.
	 *
	 * @return the styles.
	 */
	public abstract List<String> getStyles();

	/**
	 * Searches for error inside this element.
	 *
	 * @param pane     the {@link WorkingPane} where the file of this element is displayed.
	 * @param elements the {@link MipsFileElements elements of the file}.
	 */
	public abstract void searchErrors(WorkingPane pane, MipsFileElements elements);

	/**
	 * Searches for label error inside this element.
	 *
	 * @param labels the labels declared in the file.
	 * @return whether the errors have been modified.
	 */
	public abstract boolean searchLabelErrors(List<String> labels);

	/**
	 * Populates the given popup with the errors inside this element.
	 *
	 * @param popup the {@link VBox} inside the popup.
	 */
	public void populatePopupWithErrors(VBox popup) {
		Language language = Jams.getLanguageManager().getSelected();

		errors.forEach(target -> {
			String message = language.getOrDefault("EDITOR_MIPS_ERROR_" + target);
			popup.getChildren().add(new Label(message.replace("{TEXT}", getText())));
		});
	}

	/**
	 * Populates the {@link VBox} inside the popup.
	 *
	 * @param popup the {@link VBox} inside the popup.
	 */
	public abstract void populatePopup(VBox popup);


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MipsCodeElement that = (MipsCodeElement) o;
		return startIndex == that.startIndex &&
				endIndex == that.endIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startIndex, endIndex);
	}

	@Override
	public String toString() {
		return "MipsCodeElement{" +
				"startIndex=" + startIndex +
				", endIndex=" + endIndex +
				", text='" + text + '\'' +
				'}';
	}
}
