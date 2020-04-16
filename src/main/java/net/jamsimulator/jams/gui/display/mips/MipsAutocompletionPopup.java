package net.jamsimulator.jams.gui.display.mips;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.gui.display.mips.element.DisplayDirective;
import net.jamsimulator.jams.gui.display.mips.element.DisplayInstruction;
import net.jamsimulator.jams.gui.display.mips.element.MipsCodeElement;
import net.jamsimulator.jams.project.MipsProject;

public class MipsAutocompletionPopup extends Popup {

	private final MipsProject project;
	private final VBox content;

	public MipsAutocompletionPopup(MipsProject project) {
		this.project = project;
		content = new VBox();
		content.getStyleClass().add("assembly-autocompletion-popup");
		getContent().add(content);
	}

	public boolean isEmpty() {
		return content.getChildren().isEmpty();
	}

	public void refresh(MipsCodeElement element) {
		if (element instanceof DisplayDirective) refreshDirective((DisplayDirective) element);
		else if (element instanceof DisplayInstruction) refreshInstruction((DisplayInstruction) element);
	}

	private void refreshDirective(DisplayDirective element) {
		content.getChildren().clear();

		String directiveStart = element.getDirective();
		project.getDirectiveSet().getDirectives().stream().filter(target -> target.getName().startsWith(directiveStart))
				.forEach(directive -> content.getChildren().add(new Label(directive.getName())));
	}

	private void refreshInstruction(DisplayInstruction element) {
		content.getChildren().clear();

		String instructionStart = element.getText();
		project.getInstructionSet().getInstructions().stream().filter(target -> target.getMnemonic().startsWith(instructionStart))
				.forEach(directive -> content.getChildren().add(new Label(addExtraSpaces(directive.getMnemonic()) + "\t" + directive.getName())));
	}

	private String addExtraSpaces(String string) {
		StringBuilder builder = new StringBuilder(string);
		while (builder.length() < 10) builder.append(" ");
		return builder.toString();
	}


}
