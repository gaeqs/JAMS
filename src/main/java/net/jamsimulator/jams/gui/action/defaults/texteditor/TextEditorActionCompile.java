package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.Node;
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

	public static final String NAME = "EDITOR_COMPILE";

	public TextEditorActionCompile() {
		super(NAME, RegionTags.TEXT_EDITOR, null);
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
