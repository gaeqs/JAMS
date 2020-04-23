/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.mips.project.MipsProjectPane;
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
		if (hasErrors()) return Arrays.asList("mips-directive", "mips-error");
		return Collections.singletonList("mips-directive");
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
