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

package net.jamsimulator.jams.gui.mips.display;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import net.jamsimulator.jams.gui.display.popup.AutocompletionPopup;
import net.jamsimulator.jams.gui.mips.display.element.*;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.util.Set;
import java.util.stream.Stream;

public class MipsAutocompletionPopup extends AutocompletionPopup {

	private final MipsFileElements mipsElements;
	private MipsCodeElement element;

	public MipsAutocompletionPopup(MipsFileDisplay display) {
		super(display);
		this.mipsElements = display.getElements();
	}

	@Override
	public MipsFileDisplay getDisplay() {
		return (MipsFileDisplay) super.getDisplay();
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
		String start = element.getText().substring(0, Math.min(caretPosition - element.getStartIndex(), element.getText().length()));

		if (element instanceof DisplayDirective)
			start = refreshDirective(start);
		else if (element instanceof DisplayInstruction)
			start = refreshInstruction(start);
		else if (element instanceof DisplayInstructionParameterPart)
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
		addElements(project.getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directive)),
				Directive::getName, d -> "." + d.getName());
		return directive;
	}

	protected String refreshInstruction(String start) {
		MipsProject project = getDisplay().getProject().orElse(null);
		if (project == null) return start;

		Stream<Instruction> stream = project.getInstructionSet().getInstructions().stream().filter(target -> target.getMnemonic().startsWith(start));
		addElements(stream, i -> i.getMnemonic() + '\t' + i.getName(), Instruction::getMnemonic);
		return start;
	}

	protected String refreshDisplayInstructionParameterPart(String start) {
		MipsProject project = getDisplay().getProject().orElse(null);
		if (project == null) return start;

		switch (((DisplayInstructionParameterPart) element).getType()) {
			case LABEL:
			case GLOBAL_LABEL:
				addElements(mipsElements.getLabels().stream().filter(target -> target.startsWith(start)), s -> s, s -> s);
				return start;
			case REGISTER:
				Set<String> names = project.getRegistersBuilder().getRegistersNames();
				Set<Character> starts = project.getRegistersBuilder().getValidRegistersStarts();

				starts.forEach(c -> addElements(names.stream()
						.filter(target -> (c + target).startsWith(start)), s -> c + s, s -> c + s));

				return start;
			case STRING:
			case IMMEDIATE:
			default:
				return start;
		}
	}

	@Override
	public void autocomplete() {
		if (isEmpty()) return;
		String replacement = selected.getAutocompletion();

		int caretPosition = display.getCaretPosition();
		if (caretPosition == 0) return;
		MipsCodeElement element = mipsElements.getElementAt(caretPosition - 1).orElse(null);
		if (element == null) return;
		if (element.getText().substring(0, caretPosition - element.getStartIndex()).equals(replacement)) return;
		display.replaceText(element.getStartIndex(), caretPosition, replacement);
	}
}
