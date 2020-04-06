package net.jamsimulator.jams.gui.display.mips.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.utils.LabelUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayLabel extends MipsCodeElement {

	public DisplayLabel(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList("assembly-label", "assembly-error");
		return Collections.singletonList("assembly-label");
	}

	public String getLabel() {
		return text.substring(0, text.length() - 1);
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		//Illegal label
		if (text.isEmpty() || !LabelUtils.isLabelLegal(text.substring(0, text.length() - 1))) {

			System.out.println("Illegal label \"" + text.substring(0, text.length() - 1) + "\"");
			errors.add(MipsDisplayError.ILLEGAL_LABEL);
			return;
		}

		if (elements.labelCount(getLabel()) > 1) {
			errors.add(MipsDisplayError.DUPLICATE_LABEL);
		}
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

	@Override
	public void populatePopupWithErrors(VBox popup) {
		Language language = Jams.getLanguageManager().getSelected();

		errors.forEach(target -> {
			String message = language.getOrDefault("EDITOR_MIPS_ERROR_" + target);
			popup.getChildren().add(new Label(message.replace("{TEXT}", getLabel())));
		});
	}
}
