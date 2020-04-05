package net.jamsimulator.jams.gui.display.mips.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.language.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class MipsCodeElement {

	protected final int startIndex;
	protected final int endIndex;
	protected final String text;

	protected List<MipsDisplayError> errors;

	public MipsCodeElement(int startIndex, int endIndex, String text) {
		if (startIndex > endIndex) {
			throw new IllegalArgumentException("Start index (" + startIndex + ") is bigger than the end index (" + endIndex + ").");
		}
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.text = text;
		this.errors = new ArrayList<>();
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public String getText() {
		return text;
	}

	public String getLabel() {
		return text.substring(0, text.length() - 1);
	}

	public List<MipsDisplayError> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public abstract List<String> getStyles();

	public abstract void searchErrors(WorkingPane pane, MipsFileElements elements);

	public void populatePopupWithErrors(VBox popup) {
		Language language = Jams.getLanguageManager().getSelected();

		errors.forEach(target -> {
			String message = language.getOrDefault("EDITOR_MIPS_ERROR_" + target);
			popup.getChildren().add(new Label(message.replace("{TEXT}", getText())));
		});
	}

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
		return "AssemblyCodeElement{" +
				"startIndex=" + startIndex +
				", endIndex=" + endIndex +
				", text='" + text + '\'' +
				'}';
	}
}
