package net.jamsimulator.jams.gui.display.mips.element;

import javafx.scene.layout.VBox;
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

	public String getDirective() {
		return text.substring(1);
	}

	public List<DisplayDirectiveParameter> getParameters() {
		return parameters;
	}

	public void addParameter(DisplayDirectiveParameter parameter) {
		parameters.add(parameter);
	}

	public void appendReformattedCode(StringBuilder builder) {
		builder.append(text);
		parameters.forEach(target -> {
			builder.append(' ');
			builder.append(target.text);
		});
	}

	@Override
	public void move(int offset) {
		super.move(offset);
		parameters.forEach(parameter -> parameter.move(offset));
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList("assembly-directive", "assembly-error");
		return Collections.singletonList("assembly-directive");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		if (!(pane instanceof MipsProjectPane)) return;
		MipsProject project = ((MipsProjectPane) pane).getProject();
		DirectiveSet set = project.getDirectiveSet();

		Directive directive = set.getDirective(text.substring(1)).orElse(null);
		if (directive == null) {
			errors.add(MipsDisplayError.DIRECTIVE_NOT_FOUND);
		}
	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		return false;
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

}
