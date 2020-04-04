package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
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

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		//Illegal label
		if (text.isEmpty() || !LabelUtils.isLabelLegal(text.substring(0, text.length() - 1))) {

			System.out.println("Illegal label \"" + text.substring(0, text.length() - 1) + "\"");
			errors.add(MipsDisplayError.ILLEGAL_LABEL);
			return;
		}

		if (elements.getLabels().stream().filter(target -> target.text.equals(text)).count() > 1) {
			errors.add(MipsDisplayError.DUPLICATE_LABEL);
		}
	}

}
