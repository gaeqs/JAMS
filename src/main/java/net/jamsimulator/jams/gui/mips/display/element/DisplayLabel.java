package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
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
		if (hasErrors()) return Arrays.asList("mips-label", "mips-error");
		return Collections.singletonList("mips-label");
	}

	public String getLabel() {
		return text.substring(0, text.length() - 1).trim();
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		//Illegal label
		String label = getLabel();
		if (label.isEmpty() || !LabelUtils.isLabelLegal(label)) {
			errors.add(MipsDisplayError.ILLEGAL_LABEL);
			return;
		}

		if (elements.labelCount(getLabel()) > 1) {
			errors.add(MipsDisplayError.DUPLICATE_LABEL);
		}
	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		String label = getLabel();
		if (labels.stream().filter(target -> target.equals(label)).count() > 1) {
			if (errors.contains(MipsDisplayError.DUPLICATE_LABEL)) return false;
			errors.add(MipsDisplayError.DUPLICATE_LABEL);
		} else {
			if (!errors.contains(MipsDisplayError.DUPLICATE_LABEL)) return false;
			errors.remove(MipsDisplayError.DUPLICATE_LABEL);
		}
		return true;
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
