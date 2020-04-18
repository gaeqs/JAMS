package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.display.mips.MipsFileDisplay;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.MipsProject;

import java.util.Arrays;
import java.util.Collections;

public class TextEditorActionCompile extends Action {

	public static final String NAME = "TEXT_EDITOR_COMPILE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

	public TextEditorActionCompile() {
		super(NAME, RegionTags.TEXT_EDITOR, null, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Node node) {
		if (node instanceof MipsFileDisplay) {
			MipsProject project = ((MipsFileDisplay) node).getProject().orElse(null);
			if (project == null) return;
			String text = ((MipsFileDisplay) node).getText();


			Assembler assembler = project.getAssemblerBuilder().createAssembler(project.getDirectiveSet(), project.getInstructionSet(),
					new MIPS32RegisterSet(), project.getMemoryBuilder().createMemory());
			assembler.setData(Collections.singletonList(Arrays.asList(text.split("\n"))));
			assembler.compile();
			Simulation simulation = assembler.createSimulation();

			try {
				for (int i = 0; i < 1000; i++) {
					simulation.executeNextInstruction(true);
				}
			} catch (InstructionNotFoundException ignore) {
			}
			simulation.getRegisterSet().getRegister("s0").ifPresent(register -> System.out.println(register.getValue()));
		}
	}
}
