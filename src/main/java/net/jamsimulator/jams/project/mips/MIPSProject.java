/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.project.mips;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;
import net.jamsimulator.jams.gui.mips.project.MIPSStructurePane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationSource;
import net.jamsimulator.jams.project.BasicProject;
import net.jamsimulator.jams.project.ProjectType;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfigurationPresets;
import net.jamsimulator.jams.utils.RawFileData;

import java.io.File;
import java.util.LinkedList;

public class MIPSProject extends BasicProject {

    public MIPSProject(File folder) {
        super(folder, true);
    }

    @Override
    public ProjectType<?> getType() {
        return MIPSProjectType.INSTANCE;
    }

    @Override
    public MIPSProjectData getData() {
        return (MIPSProjectData) super.getData();
    }

    @Override
    public void generateSimulation(Log log) {
        var simulation = assemble(log);
        if (simulation == null) return;
        Platform.runLater(() -> getProjectTab().ifPresent(projectTab -> projectTab.getProjectTabPane()
                .createProjectPane((t, pt) -> new MIPSSimulationPane(t, pt, this, simulation), true)));
    }

    /**
     * Assembles this project, creating a {@link MIPSSimulation}.
     * <p>
     *
     * @param log The log debug messages will be print on. This log may be null.
     * @return the {@link MIPSSimulation}.
     */
    public MIPSSimulation<?> assemble(Log log) {
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
        var files = new LinkedList<RawFileData>();

        for (File target : getData().getGlobalIndex().getFiles()) {
            if (log != null) {
                log.printInfoLn("- " + target.getAbsolutePath());
            }
            try {
                files.add(new RawFileData(target, rootPath));
            } catch (Exception ex) {
                throw new AssemblerException("Error while loading file " + target + ".", ex);
            }
        }

        long nanos = System.nanoTime();

        Assembler assembler = getData().getAssemblerBuilder().createAssembler(
                files,
                getData().getDirectiveSet(),
                getData().getInstructionSet(),
                getData().getRegistersBuilder().createRegisters(getData().getInstructionSet()),
                configuration.generateNewMemory(),
                log);

        if (log != null) {
            log.println();
            log.printInfoLn("Assembling...");
        }

        assembler.assemble();

        if (log != null) {
            log.printDoneLn("Assembly successful in " + (System.nanoTime() - nanos) / 1000000 + " millis.");
        }

        var simulationData = new MIPSSimulationData(
                configuration,
                data.getFilesFolder(),
                new Console(),
                new MIPSSimulationSource(assembler.getOriginals(), assembler.getAllLabels(), assembler.getGlobalScope()),
                assembler.getInstructionSet(),
                assembler.getRegisters(),
                assembler.getMemory(),
                assembler.getStackBottom(),
                assembler.getKernelStackBottom()
        );

        simulationData.memory().saveState();
        simulationData.registers().saveState();

        return assembler.createSimulation(configuration.getNodeValue(MIPSSimulationConfigurationPresets.ARCHITECTURE), simulationData);
    }

    @Override
    public void onClose() {
        super.onClose();
        data.save();
        if (projectTab != null) {
            WorkingPane pane = projectTab.getProjectTabPane().getWorkingPane();
            if (pane instanceof MIPSStructurePane) {
                ((MIPSStructurePane) pane).getFileEditorHolder().closeAll(true);
            }
        }
    }


    @Override
    public WorkingPane generateMainProjectPane(Tab tab, ProjectTab projectTab) {
        return new MIPSStructurePane(tab, projectTab, this);
    }

    @Override
    protected void loadData() {
        data = new MIPSProjectData(this);
        data.load();
    }
}
