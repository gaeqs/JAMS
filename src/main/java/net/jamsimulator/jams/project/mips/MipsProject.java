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

import net.jamsimulator.jams.gui.mips.project.MipsStructurePane;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.BasicProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MipsProject extends BasicProject {

	public MipsProject(File folder) {
		super(folder, false);
		loadData(null);
	}

	public MipsProject(String name, File folder) {
		super(folder, false);
		loadData(name);
	}

	@Override
	public MipsProjectData getData() {
		return (MipsProjectData) super.getData();
	}

	@Override
	public Simulation<?> assemble() throws IOException {
		MipsSimulationConfiguration selected = getData().getSelectedConfiguration().orElse(null);
		if (selected == null) return null;

		List<String> files = new ArrayList<>();

		for (File target : getData().getFilesToAssemble().getFiles()) {
			files.add(String.join("\n", Files.readAllLines(target.toPath())));
		}

		Assembler assembler = getData().getAssemblerBuilder().createAssembler(
				files,
				getData().getDirectiveSet(),
				getData().getInstructionSet(),
				getData().getRegistersBuilder().createRegisters(),
				selected.getMemoryBuilder().createMemory());

		assembler.assemble();

		//TODO ADD EXECUTIONS
		return assembler.createSimulation(selected.getArchitecture(), null, data.getFilesFolder(), new Console());
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
	protected void loadData(String name) {
		data = new MipsProjectData(this);
		data.load();

		if (name != null) {
			data.setName(name);
		}

		data.save();
	}
}
