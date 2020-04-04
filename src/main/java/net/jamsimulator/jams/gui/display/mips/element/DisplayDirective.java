package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.project.MipsProjectPane;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.project.MipsProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayDirective extends MipsCodeElement {

	private final List<DisplayDirectiveParameter> parameters;

	public DisplayDirective(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		parameters = new ArrayList<>();
	}


	public List<DisplayDirectiveParameter> getParameters() {
		return parameters;
	}

	public void addParameter(DisplayDirectiveParameter parameter) {
		parameters.add(parameter);
	}

	@Override
	public List<String> getStyles() {
		if(hasErrors()) return Arrays.asList("assembly-directive", "assembly-error");
		return Collections.singletonList("assembly-directive");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		if (!(pane instanceof MipsProjectPane)) return;
		MipsProject project = ((MipsProjectPane) pane).getProject();
		DirectiveSet set = project.getDirectiveSet();

		Directive directive = set.getDirective(text.substring(1)).orElse(null);
		if(directive == null) {
			errors.add(MipsDisplayError.DIRECTIVE_NOT_FOUND);
		}
	}

}
