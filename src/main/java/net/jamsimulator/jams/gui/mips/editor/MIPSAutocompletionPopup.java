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

package net.jamsimulator.jams.gui.mips.editor;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import net.jamsimulator.jams.gui.editor.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.mips.editor.element.*;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class MIPSAutocompletionPopup extends AutocompletionPopup {

	private final MIPSFileElements mipsElements;
	private MIPSCodeElement element;

	public MIPSAutocompletionPopup(MIPSFileEditor display) {
		super(display);
		this.mipsElements = display.getElements();
	}

	@Override
	public MIPSFileEditor getDisplay() {
		return (MIPSFileEditor) super.getDisplay();
	}

	@Override
	public void execute(int caretOffset, boolean autocompleteIfOne) {
		int caretPosition = display.getCaretPosition() + caretOffset;
		if (caretPosition <= 0) return;
		element = mipsElements.getElementAt(caretPosition - 1).orElse(null);
		if (element == null) {
			hide();
			return;
		}

		refreshContents(caretPosition);
		if (isEmpty()) {
			hide();
			return;
		}
		if (autocompleteIfOne && size() == 1) {
			hide();
			autocomplete();
		} else {
			Platform.runLater(() -> {
				Bounds bounds = display.getCaretBounds().orElse(null);
				if (bounds == null) return;
				show(display, bounds.getMinX(), bounds.getMinY() + 20);
			});
		}
	}

	@Override
	public void refreshContents(int caretPosition) {
		elements.clear();
		String start = element.getSimpleText();

		if (element instanceof MIPSDirective)
			start = refreshDirective(start);
		else if (element instanceof MIPSInstruction)
			start = refreshInstruction(start);
		else if (element instanceof MIPSInstructionParameterPart)
			start = refreshDisplayInstructionParameterPart(start);

		sortAndShowElements(start);

		if (isEmpty()) return;
		selectedIndex = 0;
		refreshSelected();
	}

	protected String refreshDirective(String start) {
		MipsProject project = getDisplay().getProject().orElse(null);
		if (project == null) return start;

		String directive = start.substring(1);
		addElements(project.getData().getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directive)),
				Directive::getName, d -> "." + d.getName());
		return directive;
	}

	protected String refreshInstruction(String start) {
		MipsProject project = getDisplay().getProject().orElse(null);
		if (project == null) return start;

		Stream<Instruction> stream = project.getData().getInstructionSet().getInstructions().stream().filter(target -> target.getMnemonic().startsWith(start));
		addElements(stream, i -> i.getMnemonic() + " \t"
				+ StringUtils.addSpaces(parseParameters(i.getParameters()), 25, true)
				+ i.getName(), Instruction::getMnemonic);
		return start;
	}

	protected String refreshDisplayInstructionParameterPart(String start) {
		MipsProject project = getDisplay().getProject().orElse(null);
		if (project == null) return start;

		switch (((MIPSInstructionParameterPart) element).getType()) {
			case LABEL:
			case GLOBAL_LABEL:
				Set<String> labels = new HashSet<>(mipsElements.getLabels());
				mipsElements.getFilesToAssemble().ifPresent(files -> labels.addAll(files.getGlobalLabels()));
				addElements(labels.stream().filter(target -> target.startsWith(start)), s -> s, s -> s);
				return start;
			case REGISTER:
				Set<String> names = project.getData().getRegistersBuilder().getRegistersNames();
				Set<Character> starts = project.getData().getRegistersBuilder().getValidRegistersStarts();

				starts.forEach(c -> addElements(names.stream()
						.filter(target -> (c + target).startsWith(start)), s -> c + s, s -> c + s));

				return start;
			case STRING:
			case IMMEDIATE:
			default:
				return start;
		}
	}

	protected String parseParameters(ParameterType[] types) {
		StringBuilder builder = new StringBuilder();
		for (ParameterType type : types) {
			builder.append(type.getExample()).append(" ");
		}
		return builder.length() == 0 ? " " : builder.substring(0, builder.length() - 1);
	}

	@Override
	public void autocomplete() {
		if (isEmpty()) return;
		String replacement = selected.getAutocompletion();

		int caretPosition = display.getCaretPosition();
		if (caretPosition == 0) return;
		MIPSCodeElement element = mipsElements.getElementAt(caretPosition - 1).orElse(null);
		if (element == null) return;
		if (element.getText().substring(0, caretPosition - element.getStartIndex()).equals(replacement)) return;

		boolean addSpace = element instanceof MIPSInstruction || element instanceof MIPSDirective;

		display.replaceText(element.getStartIndex(), caretPosition, addSpace ? replacement + " " : replacement);
	}
}
