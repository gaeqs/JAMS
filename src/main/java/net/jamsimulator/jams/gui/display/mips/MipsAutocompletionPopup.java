package net.jamsimulator.jams.gui.display.mips;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.mips.element.*;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.project.MipsProject;
import net.jamsimulator.jams.utils.CharacterCodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MipsAutocompletionPopup extends Popup {

	private final MipsProject project;
	private final MipsFileElements elements;
	private final VBox content;

	public MipsAutocompletionPopup(MipsProject project, MipsFileElements elements) {
		this.project = project;
		this.elements = elements;
		content = new VBox();
		content.getStyleClass().add("assembly-autocompletion-popup");
		getContent().add(content);
	}

	public boolean isEmpty() {
		return content.getChildren().isEmpty();
	}

	public void managePressEvent(KeyEvent event, MipsFileDisplay display) {
		byte b = event.getCharacter().getBytes()[0];
		//ESCAPE OR SPACE
		if (b == CharacterCodes.ENTER) return;
		if (b == CharacterCodes.ESCAPE || b == CharacterCodes.SPACE) {
			hide();
		} else {
			refresh(display, 0);
		}
	}

	public boolean manageTypeEvent(KeyEvent event, MipsFileDisplay display) {
		if (!isShowing()) return false;
		if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.BACK_SPACE) {
			refresh(display, event.getCode() == KeyCode.RIGHT ? 1 : -1);
			return false;
		}
		if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
			return true;
			//TODO SELECT ELEMENT
		}
		if (event.getCode() == KeyCode.ENTER) {
			autocomplete(display);
			hide();
			return true;
		}

		return false;
	}

	public void refresh(MipsFileDisplay display, int caretOffset) {
		int caretPosition = display.getCaretPosition() + caretOffset;
		if (caretPosition <= 0) return;
		MipsCodeElement element = elements.getElementAt(caretPosition - 1).orElse(null);
		if (element == null) {
			hide();
			return;
		}

		refreshContents(element, caretPosition);
		if (isEmpty()) {
			hide();
			return;
		}

		Platform.runLater(() -> {
			Bounds bounds = display.getCaretBounds().orElse(null);
			if (bounds == null) return;
			show(display, bounds.getMinX(), bounds.getMinY() + 20);
		});
	}

	public void refreshContents(MipsCodeElement element, int caretPosition) {
		content.getChildren().clear();
		String start = element.getText().substring(0, Math.min(caretPosition - element.getStartIndex(), element.getText().length()));

		if (element instanceof DisplayDirective)
			refreshDirective((DisplayDirective) element, start);
		else if (element instanceof DisplayInstruction)
			refreshInstruction((DisplayInstruction) element, start);
		else if (element instanceof DisplayInstructionParameterPart)
			refreshDisplayInstructionParameterPart((DisplayInstructionParameterPart) element, start);
	}

	public void autocomplete(MipsFileDisplay display) {
		if (isEmpty()) return;
		String replacement = ((AutocompletionLabel) content.getChildren().get(0)).getAutocompletion();

		int caretPosition = display.getCaretPosition();
		if (caretPosition == 0) return;
		MipsCodeElement element = elements.getElementAt(caretPosition - 1).orElse(null);
		if (element == null) return;
		if(element.getText().equals(replacement)) return;
		display.replaceText(element.getStartIndex(), element.getEndIndex(), replacement);
	}

	private void refreshDirective(DisplayDirective element, String start) {
		String directive = start.substring(1);
		addElements(project.getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directive)),
				Directive::getName, d -> "." + d.getName());
	}

	private void refreshInstruction(DisplayInstruction element, String start) {
		Stream<Instruction> stream = project.getInstructionSet().getInstructions().stream().filter(target -> target.getMnemonic().startsWith(start));
		addElements(stream, i -> i.getMnemonic() + '\t' + i.getName(), Instruction::getMnemonic);
	}

	private void refreshDisplayInstructionParameterPart(DisplayInstructionParameterPart element, String start) {
		switch (element.getType()) {
			case LABEL:
				addElements(elements.getLabels().stream().filter(target -> target.startsWith(start)), s -> s, s -> s);
				break;
			case REGISTER:
				String registerName = start.substring(1);
				//TODO change this
				RegisterSet registerSet = new MIPS32RegisterSet();
				Set<String> names = registerSet.getRegisters().stream().map(Register::getNames)
						.flatMap(Collection::stream).collect(Collectors.toSet());
				addElements(names.stream().filter(target -> target.startsWith(registerName)), s -> "$" + s, s -> "$" + s);
				break;
			case STRING:
			case IMMEDIATE:
				break;
		}
	}


	private <T> void addElements(Collection<T> collection, Function<T, String> conversion,
								 Function<T, String> autocompletionConversion) {
		addElements(collection.iterator(), conversion, autocompletionConversion);
	}

	private <T> void addElements(Stream<T> collection, Function<T, String> conversion,
								 Function<T, String> autocompletionConversion) {
		addElements(collection.iterator(), conversion, autocompletionConversion);
	}

	private <T> void addElements(Iterator<T> iterator, Function<T, String> conversion,
								 Function<T, String> autocompletionConversion) {
		int i = 0;
		Label label;
		T next;
		while (iterator.hasNext() && i < 5) {
			next = iterator.next();
			label = new AutocompletionLabel(addExtraSpaces(conversion.apply(next)), autocompletionConversion.apply(next));
			content.getChildren().add(label);
			i++;
		}
	}


	private String addExtraSpaces(String string) {
		StringBuilder builder = new StringBuilder(string);
		while (builder.length() < 10) builder.append(" ");
		return builder.toString();
	}

	private static class AutocompletionLabel extends Label {

		private final String autocompletion;

		public AutocompletionLabel(String name, String autocompletion) {
			super(name);
			this.autocompletion = autocompletion;
		}

		public String getAutocompletion() {
			return autocompletion;
		}
	}


}
