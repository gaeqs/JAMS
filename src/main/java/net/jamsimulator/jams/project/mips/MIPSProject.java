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

import net.jamsimulator.jams.gui.mips.project.MIPSStructurePane;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;
import net.jamsimulator.jams.project.BasicProject;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;
import net.jamsimulator.jams.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MIPSProject extends BasicProject {

	public MIPSProject(File folder) {
		super(folder, false);
		loadData(null);
	}

	public MIPSProject(String name, File folder) {
		super(folder, false);
		loadData(name);
	}

	@Override
	public MIPSProjectData getData() {
		return (MIPSProjectData) super.getData();
	}

	@Override
	public Simulation<?> assemble(Log log) throws IOException {
		MIPSSimulationConfiguration configuration = getData().getSelectedConfiguration().orElse(null);

		if (configuration == null) {
			if (log != null) {
				log.printErrorLn("Error! Configuration not found!");
			}
			return null;
		}

		if (log != null) {
			log.printInfoLn("Assembling project \"" + data.getName() + "\" using configuration \"" + configuration.getName() + "\".");
			log.println();
			log.printInfoLn("Files:");
		}

		var rootPath = folder.toPath();
		var files = new HashMap<String, String>();

		for (File target : getData().getFilesToAssemble().getFiles()) {
			if (log != null) {
				log.printInfoLn("- " + target.getAbsolutePath());
			}

			var name = rootPath.relativize(target.toPath()).toString();
			try {
				files.put(name, FileUtils.readAll(target));
			} catch (Exception ex) {
				throw new AssemblerException("Error while loading file " + target + ".", ex);
			}
		}

		long nanos = System.nanoTime();

		Assembler assembler = getData().getAssemblerBuilder().createAssembler(
				files,
				getData().getDirectiveSet(),
				getData().getInstructionSet(),
				getData().getRegistersBuilder().createRegisters(),
				configuration.generateNewMemory());

		if (log != null) {
			log.println();
			log.printInfoLn("Assembling...");
		}

		assembler.assemble();

		if (log != null) {
			log.printDoneLn("Assembly successful in " + (System.nanoTime() - nanos) / 1000000 + " millis.");
		}

		var simulationData = new SimulationData(configuration, data.getFilesFolder(), new Console(),
				assembler.getOriginals(), assembler.getLabelsWithFileNames());

		return assembler.createSimulation(configuration.getNodeValue(MIPSSimulationConfigurationPresets.ARCHITECTURE), simulationData);
	}

	@Override
	public void onClose() {
		data.save();
		if (projectTab != null) {
			WorkingPane pane = projectTab.getProjectTabPane().getWorkingPane();
			if (pane instanceof MIPSStructurePane) {
				((MIPSStructurePane) pane).getFileDisplayHolder().closeAll(true);
			}
		}
	}


	@Override
	protected void loadData(String name) {
		data = new MIPSProjectData(this);
		data.load();

		if (name != null) {
			data.setName(name);
		}

		data.save();
	}
}
