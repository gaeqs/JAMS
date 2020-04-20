package net.jamsimulator.jams.gui.display.mips;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import net.jamsimulator.jams.gui.display.mips.element.*;
import net.jamsimulator.jams.gui.display.popup.AutocompletionPopup;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.project.MipsProject;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
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
				addElements(mipsElements.getLabels().stream().filter(target -> target.startsWith(start)), s -> s, s -> s);
				return start;
			case REGISTER:
				String registerName = start.substring(1);
				//TODO change this
				RegisterSet registerSet = new MIPS32RegisterSet();
				Set<String> names = registerSet.getRegisters().stream().map(Register::getNames)
						.flatMap(Collection::stream).collect(Collectors.toSet());
				addElements(names.stream().filter(target -> target.startsWith(registerName)), s -> "$" + s, s -> "$" + s);
				return registerName;
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
