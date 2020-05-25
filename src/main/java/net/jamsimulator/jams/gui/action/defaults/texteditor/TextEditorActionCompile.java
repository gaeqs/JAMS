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

package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.mips.display.MIPSFileEditor;
import net.jamsimulator.jams.gui.mips.project.MipsWorkingPane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TextEditorActionCompile extends Action {

	public static final String NAME = "TEXT_EDITOR_COMPILE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

	public TextEditorActionCompile() {
		super(NAME, RegionTags.TEXT_EDITOR, Messages.ACTION_TEXT_EDITOR_COMPILE, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Object node) {
		try {
			if (node instanceof MIPSFileEditor) {
				MipsProject project = ((MIPSFileEditor) node).getProject().orElse(null);
				if (project == null) return;

				ProjectTab tab = project.getProjectTab().orElse(null);
				if (tab != null) {
					MipsWorkingPane pane = (MipsWorkingPane) tab.getProjectTabPane().getWorkingPane();
					pane.getFileDisplayHolder().saveAll(true);
				}

				List<List<String>> files = new ArrayList<>();
				for (File file : project.getData().getFilesToAssemble().getFiles()) {
					files.add(Files.readAllLines(file.toPath()));
					System.out.println("- FILE: " + file);
					for (String line : Files.readAllLines(file.toPath())) {
						System.out.println(line);
					}
				}


				Assembler assembler = project.getData().getAssemblerBuilder().createAssembler(project.getData().getDirectiveSet(), project.getData().getInstructionSet(),
						new MIPS32Registers(), project.getData().getMemoryBuilder().createMemory());
				assembler.setData(files);
				assembler.compile();
				Simulation<?> simulation = assembler.createSimulation(project.getData().getArchitecture());

				try {
					for (int i = 0; i < 1000; i++) {
						simulation.nextStep();
					}
				} catch (InstructionNotFoundException ignore) {
				}
				simulation.getRegisterSet().getRegister("s0").ifPresent(register -> System.out.println(register.getValue()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
