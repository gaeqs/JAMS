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

package net.jamsimulator.jams.project.mips;

import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.mips.project.MipsStructurePane;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.BasicProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MipsProject extends BasicProject {

	public MipsProject(String name, File folder, Architecture architecture, AssemblerBuilder assemblerBuilder, MemoryBuilder memoryBuilder,
					   RegistersBuilder registersBuilder, DirectiveSet directiveSet, InstructionSet instructionSet) {
		super(name, folder, false);
		loadData(architecture, assemblerBuilder, memoryBuilder, registersBuilder, directiveSet, instructionSet);
	}

	public MipsProject(String name, File folder) {
		super(name, folder, false);
		loadData();
	}

	@Override
	public MipsProjectData getData() {
		return (MipsProjectData) super.getData();
	}

	@Override
	public Simulation<?> assemble() throws IOException {
		Assembler assembler = getData().getAssemblerBuilder().createAssembler(
				getData().getDirectiveSet(),
				getData().getInstructionSet(),
				getData().getRegistersBuilder().createRegisters(),
				getData().getMemoryBuilder().createMemory());

		List<List<String>> files = new ArrayList<>();

		for (File target : getData().getFilesToAssemble().getFiles()) {
			files.add(Files.readAllLines(target.toPath()));
		}

		assembler.setData(files);
		assembler.compile();
		return assembler.createSimulation(getData().getArchitecture());
	}

	@Override
	public void onClose() {
		data.save();
		if (projectTab != null) {
			WorkingPane pane = projectTab.getProjectTabPane().getWorkingPane();
			if (pane instanceof MipsStructurePane) {
				((MipsStructurePane) pane).getFileDisplayHolder().closeAll(true);
			}
		}
	}


	@Override
	protected void loadData() {
		data = new MipsProjectData(this);
		data.load();
		data.save();
	}

	protected void loadData(Architecture architecture, AssemblerBuilder assemblerBuilder, MemoryBuilder memoryBuilder,
							RegistersBuilder registersBuilder, DirectiveSet directiveSet, InstructionSet instructionSet) {
		data = new MipsProjectData(this);
		((MipsProjectData) data).load(architecture, assemblerBuilder, memoryBuilder, registersBuilder, directiveSet, instructionSet);
	}
}
