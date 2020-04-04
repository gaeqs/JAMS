package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

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
		if(hasErrors()) return Arrays.asList(string ? "assembly-directive-parameter-string" : "assembly-directive-parameter", "assembly-error");
		return Collections.singletonList(string ? "assembly-directive-parameter-string" : "assembly-directive-parameter");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		if (NumericUtils.isInteger(text)) return;
		if (StringUtils.isStringOrChar(text)) return;
		errors.add(MipsDisplayError.INVALID_DIRECTIVE_PARAMETER);
	}

}
