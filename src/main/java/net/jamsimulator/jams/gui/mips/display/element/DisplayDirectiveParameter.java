package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayDirectiveParameter extends MipsCodeElement {

	private final DisplayDirective directive;
	private final int parameterIndex;
	private final boolean string;

	public DisplayDirectiveParameter(DisplayDirective directive, int parameterIndex,
									 int startIndex, int endIndex, String text, boolean string) {
		super(startIndex, endIndex, text);
		this.directive = directive;
		this.parameterIndex = parameterIndex;
		this.string = string;
	}

	public DisplayDirective getDirective() {
		return directive;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors())
			return Arrays.asList(string ? "mips-directive-parameter-string" : "mips-directive-parameter", "assembly-error");
		return Collections.singletonList(string ? "mips-directive-parameter-string" : "mips-directive-parameter");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		if (string || NumericUtils.isInteger(text)) return;
		if (!elements.hasLabel(text)) {
			errors.add(MipsDisplayError.LABEL_NOT_FOUND);
		}
	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		if (string || NumericUtils.isInteger(text)) return false;
		if (labels.contains(text)) {
			if (!errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return false;
			errors.remove(MipsDisplayError.LABEL_NOT_FOUND);
		} else {
			if (errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return false;
			errors.add(MipsDisplayError.LABEL_NOT_FOUND);
		}
		return true;
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

}
