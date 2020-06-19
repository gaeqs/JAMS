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
import net.jamsimulator.jams.gui.mips.project.MipsSimulatorPane;
import net.jamsimulator.jams.gui.mips.project.MipsStructurePane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.util.Log;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SingleCycleSimulation;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.project.mips.MipsSimulationConfiguration;
import net.jamsimulator.jams.utils.NumericUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TextEditorActionCompile extends Action {

	public static final String NAME = "TEXT_EDITOR_COMPILE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

	public TextEditorActionCompile() {
		super(NAME, RegionTags.TEXT_EDITOR, Messages.ACTION_TEXT_EDITOR_COMPILE, DEFAULT_COMBINATION);
	}

	@Override
	public void run(Object node) {
		if (node instanceof MIPSFileEditor) {
			MipsProject project = ((MIPSFileEditor) node).getProject().orElse(null);
			if (project == null) return;
			compileAndShow(project);
		}
	}

	public static void compileAndShow(MipsProject project) {
		ProjectTab tab = project.getProjectTab().orElse(null);
		if (tab == null) return;
		MipsStructurePane pane = (MipsStructurePane) tab.getProjectTabPane().getWorkingPane();
		pane.getFileDisplayHolder().saveAll(true);
		pane.getLogButton().setSelected(true);
		Log log = pane.getLog();
		try {
			List<List<String>> files = new ArrayList<>();
			for (File file : project.getData().getFilesToAssemble().getFiles()) {
				files.add(Files.readAllLines(file.toPath()));
				log.printInfoLn("- FILE: " + file);
				for (String line : Files.readAllLines(file.toPath())) {
					log.println(line);
				}
			}

			MipsSimulationConfiguration selected = project.getData().getSelectedConfiguration().orElse(null);
			if (selected == null) {
				log.printErrorLn("Configuration not found!");
				return;
			}

			Assembler assembler = project.getData().getAssemblerBuilder().createAssembler(project.getData().getDirectiveSet(), project.getData().getInstructionSet(),
					new MIPS32Registers(), selected.getMemoryBuilder().createMemory());
			assembler.setData(files);
			assembler.compile();

			int mainLabel = assembler.getGlobalLabels().getOrDefault("main", -1);
			Simulation<?> simulation = assembler.createSimulation(selected.getArchitecture());

			project.getProjectTab().ifPresent(projectTab ->
					projectTab.getProjectTabPane()
							.createProjectPane((t, pt) -> new MipsSimulatorPane(t, pt, project, simulation), true));

			if (simulation instanceof SingleCycleSimulation) {
				((SingleCycleSimulation) simulation).setLog(log);
			}

			log.println();
			if (mainLabel == -1) {
				log.printWarningLn("Global label \"main\" not found. Staring at the start of the text section.");
			} else {
				log.printInfoLn("Global label \"main\" found. Starting simulaiton at this location.");
				simulation.getRegisterSet().getProgramCounter().setValue(mainLabel);
			}

			log.println();
			log.printInfoLn("SIMULATION:");
			log.println();

			try {
				for (int i = 0; i < 1000; i++) {
					simulation.nextStep();
				}
			} catch (InstructionNotFoundException ignore) {
			}

			log.println();
			log.printDoneLn("FINISHED");
			log.println("_________________________________________");
			log.println();
			log.printInfoLn("RESULTS:");
			log.println();

			log.printInfoLn("REGISTERS:");
			log.println();
			log.printInfo("\tPC");
			log.print(": 0x" + toHexFill(simulation.getRegisterSet().getProgramCounter().getValue()));
			log.printDoneLn("\t" + simulation.getRegisterSet().getProgramCounter().getValue());
			log.println();

			simulation.getRegisterSet().getGeneralRegisters().stream()
					.sorted(Comparator.comparingInt(Register::getIdentifier))
					.forEach(register -> {
						String name = register.getNames().stream().filter(target -> !NumericUtils.isInteger(target)).findFirst().orElse("-");
						log.printInfo("\t" + name);
						log.print(": 0x" + toHexFill(register.getValue()));
						log.printDoneLn("\t " + register.getValue());
					});
		} catch (Exception ex) {
			log.printErrorLn("ERROR:");
			log.printErrorLn(ex.getMessage());
		}
	}

	private static String toHexFill(int i) {
		return addZeros(Integer.toHexString(i), 8);
	}

	private static String addZeros(String s, int to) {
		StringBuilder builder = new StringBuilder();
		int max = Math.max(0, to - s.length());

		for (int i = 0; i < max; i++) {
			builder.append("0");
		}

		return builder + s;
	}
}
