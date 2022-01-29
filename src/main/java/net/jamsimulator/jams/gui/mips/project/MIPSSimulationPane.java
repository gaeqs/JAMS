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

package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.bar.BarPosition;
import net.jamsimulator.jams.gui.bar.BarSnapshot;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewMode;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.simulator.cache.CacheVisualizer;
import net.jamsimulator.jams.gui.mips.simulator.execution.ExecutionButtons;
import net.jamsimulator.jams.gui.mips.simulator.execution.SpeedSlider;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.information.SimulationInformationBuilder;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionViewerGroup;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.lab.LabPane;
import net.jamsimulator.jams.gui.mips.simulator.label.LabelTable;
import net.jamsimulator.jams.gui.mips.simulator.memory.MemoryPane;
import net.jamsimulator.jams.gui.mips.simulator.register.COP0RegistersTable;
import net.jamsimulator.jams.gui.mips.simulator.register.RegistersTable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.SimulationHolder;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.gui.util.ZoomUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.project.mips.MIPSProject;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.HashSet;
import java.util.Set;

public class MIPSSimulationPane extends WorkingPane implements SimulationHolder<Integer>, ActionRegion {

    protected final ExecutionButtons executionButtons;
    protected final InstructionViewerGroup instructionViewerGroup;
    protected MIPSProject project;
    protected MIPSSimulation<?> simulation;
    protected TabPane registersTabs;
    protected MemoryPane memoryPane;


    public MIPSSimulationPane(Tab parent, ProjectTab projectTab, MIPSProject project, MIPSSimulation<?> simulation) {
        super(parent, projectTab, null, false);
        this.project = project;
        this.simulation = simulation;
        this.executionButtons = new ExecutionButtons(simulation);


        var user = MIPSAssembledCodeViewer.createViewer(simulation.getArchitecture(), simulation, false);

        var userScale = new ScaledVirtualized<>(user);
        ZoomUtils.applyZoomListener(user, userScale);

        if (Integer.compareUnsigned(simulation.getKernelStackBottom(), MIPS32Memory.EXCEPTION_HANDLER) >= 0) {
            var kernel = MIPSAssembledCodeViewer.createViewer(simulation.getArchitecture(), simulation, true);

            var kernelScale = new ScaledVirtualized<>(kernel);
            ZoomUtils.applyZoomListener(kernel, kernelScale);

            TabPane pane = new TabPane();
            Tab userTab = new LanguageTab(Messages.INSTRUCTIONS_USER, new VirtualizedScrollPane<>(userScale));
            Tab kernelTab = new LanguageTab(Messages.INSTRUCTIONS_KERNEL, new VirtualizedScrollPane<>(kernelScale));
            userTab.setClosable(false);
            kernelTab.setClosable(false);
            pane.getTabs().addAll(userTab, kernelTab);
            center = pane;
            instructionViewerGroup = new InstructionViewerGroup(user, kernel, userTab, kernelTab, pane);
        } else {
            center = new VirtualizedScrollPane<>(userScale);
            instructionViewerGroup = new InstructionViewerGroup(user);
        }

        init();

        loadRegisterTabs();
        loadConsole();
        loadMemoryTab();
        loadFlow();
        loadLabels();
        loadCacheVisualizer();
        loadLab();
        loadInformation();

        SplitPane.setResizableWithParent(center, true);
    }

    public MIPSProject getProject() {
        return project;
    }

    @Override
    public MIPSSimulation<?> getSimulation() {
        return simulation;
    }

    public InstructionViewerGroup getInstructionTableGroup() {
        return instructionViewerGroup;
    }

    public MemoryPane getMemoryPane() {
        return memoryPane;
    }

    private void loadRegisterTabs() {
        var icon = Icons.SIMULATION_REGISTERS;
        registersTabs = new TabPane();


        Set<Register> general = new HashSet<>(simulation.getRegisters().getGeneralRegisters());
        general.add(simulation.getRegisters().getProgramCounter());

        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_GENERAL, new RegistersTable(simulation, general, false)));
        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP0, new COP0RegistersTable(simulation, simulation.getRegisters().getCoprocessor0Registers(), false)));
        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP1, new RegistersTable(simulation, simulation.getRegisters().getCoprocessor1Registers(), true)));

        registersTabs.getTabs().forEach(tab -> tab.setClosable(false));

        manageBarAddition("registers", registersTabs, icon, Messages.BAR_REGISTERS_NAME, BarPosition.RIGHT_TOP, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadConsole() {
        var icon = Icons.SIMULATION_CONSOLE;
        manageBarAddition("console", simulation.getConsole(), icon, Messages.BAR_CONSOLE_NAME, BarPosition.BOTTOM_LEFT, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadMemoryTab() {
        var icon = Icons.SIMULATION_MEMORY;
        memoryPane = new MemoryPane(simulation);
        manageBarAddition("memory", memoryPane, icon, Messages.BAR_MEMORY_NAME, BarPosition.LEFT_TOP, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadFlow() {
        var icon = Icons.SIMULATION_FLOW;
        FlowTable flow = FlowTable.createFlow(simulation);
        manageBarAddition("flow", flow, icon, Messages.BAR_FLOW_NAME, BarPosition.BOTTOM_RIGHT, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadLabels() {
        var icon = Icons.SIMULATION_LABELS;
        var scroll = new PixelScrollPane(null);
        var pane = new LabelTable(scroll, this);
        scroll.setContent(pane);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);

        manageBarAddition("labels", scroll, icon, Messages.BAR_LABELS_NAME, BarPosition.RIGHT_BOTTOM, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadCacheVisualizer() {
        var icon = Icons.SIMULATION_CACHES;
        var memory = simulation.getMemory();
        if (memory instanceof Cache) {
            var visualizer = new CacheVisualizer(simulation);
            manageBarAddition("cache_visualizer", visualizer, icon, Messages.BAR_CACHES_NAME, BarPosition.LEFT_BOTTOM, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
        }
    }

    private void loadLab() {
        var icon = Icons.SIMULATION_LAB;
        var lab = new LabPane(simulation);
        var scroll = new PixelScrollPane(lab);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        manageBarAddition("lab", scroll, icon, Messages.BAR_LAB_NAME, BarPosition.LEFT_BOTTOM, Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);
    }

    private void loadInformation() {
        var m = Manager.of(SimulationInformationBuilder.class);

        var icon = Icons.SIMULATION_INFO;
        var builder = m.get(simulation.getArchitecture().getName())
                .orElseGet(() -> m.get(SingleCycleArchitecture.NAME).orElse(null));
        if (builder == null) return;
        var info = builder.buildNewNode(simulation);
        var scroll = new PixelScrollPane(info);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        manageBarAddition("information", scroll, icon, Messages.BAR_INFO_NAME, BarPosition.RIGHT_BOTTOM,
                Manager.ofD(BarSnapshotViewMode.class).getDefault(), true);

    }


    private void manageBarAddition(String name, Node node, IconData icon, String languageNode, BarPosition defaultPosition,
                                   BarSnapshotViewMode defaultViewMode, boolean defaultEnable) {
        barMap.registerSnapshot(new BarSnapshot(name, node, defaultPosition, defaultViewMode, defaultEnable, icon, languageNode));
    }


    @Override
    public String getLanguageNode() {
        return Messages.PROJECT_TAB_SIMULATION;
    }

    @Override
    public void saveAllOpenedFiles() {
        // There's nothing to save!
    }

    @Override
    public void onClose() {
        super.onClose();
        simulation.stop();
    }

    @Override
    public void populateHBox(HBox buttonsHBox) {
        buttonsHBox.getChildren().clear();
        buttonsHBox.getChildren().addAll(new SpeedSlider(simulation));
        buttonsHBox.getChildren().addAll(executionButtons.getNodes());
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }
}
