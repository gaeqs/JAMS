package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.bar.BarPaneSnapshot;
import net.jamsimulator.jams.gui.bar.BarPosition;
import net.jamsimulator.jams.gui.bar.mode.BarSnapshotViewModePane;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.simulator.cache.CacheVisualizer;
import net.jamsimulator.jams.gui.mips.simulator.execution.ExecutionButtons;
import net.jamsimulator.jams.gui.mips.simulator.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.instruction.InstructionViewerGroup;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledCodeViewer;
import net.jamsimulator.jams.gui.mips.simulator.label.LabelTable;
import net.jamsimulator.jams.gui.mips.simulator.memory.MemoryPane;
import net.jamsimulator.jams.gui.mips.simulator.register.COP0RegistersTable;
import net.jamsimulator.jams.gui.mips.simulator.register.RegistersTable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.ZoomUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MIPSProject;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.HashSet;
import java.util.Set;

public class MIPSSimulationPane extends WorkingPane implements ActionRegion {

    protected MIPSProject project;
    protected Simulation<?> simulation;
    protected TabPane registersTabs;

    protected final ExecutionButtons executionButtons;
    protected final InstructionViewerGroup instructionViewerGroup;
    protected MemoryPane memoryPane;


    public MIPSSimulationPane(Tab parent, ProjectTab projectTab, MIPSProject project, Simulation<?> simulation) {
        super(parent, projectTab, null, false);
        this.project = project;
        this.simulation = simulation;
        this.executionButtons = new ExecutionButtons(simulation);


        var user = MIPSAssembledCodeViewer.createViewer(simulation.getArchitecture(), simulation, false);

        var userScale = new ScaledVirtualized<>(user);
        ZoomUtils.applyZoomListener(user, userScale);

        if (Integer.compareUnsigned(simulation.getKernelStackBottom(), MIPS32Memory.EXCEPTION_HANDLER) > 0) {
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

        SplitPane.setResizableWithParent(center, true);
    }

    public MIPSProject getProject() {
        return project;
    }

    public Simulation<?> getSimulation() {
        return simulation;
    }

    public InstructionViewerGroup getInstructionTableGroup() {
        return instructionViewerGroup;
    }

    public MemoryPane getMemoryPane() {
        return memoryPane;
    }

    private void loadRegisterTabs() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_REGISTERS
        ).orElse(null);

        registersTabs = new TabPane();


        Set<Register> general = new HashSet<>(simulation.getRegisters().getGeneralRegisters());
        general.add(simulation.getRegisters().getProgramCounter());

        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_GENERAL, new RegistersTable(simulation, general, false)));
        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP0, new COP0RegistersTable(simulation, simulation.getRegisters().getCoprocessor0Registers(), false)));
        registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP1, new RegistersTable(simulation, simulation.getRegisters().getCoprocessor1Registers(), true)));

        registersTabs.getTabs().forEach(tab -> tab.setClosable(false));

        manageBarAddition("registers", registersTabs, icon, Messages.BAR_REGISTERS_NAME, BarPosition.RIGHT_TOP, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadConsole() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CONSOLE
        ).orElse(null);

        manageBarAddition("console", simulation.getConsole(), icon, Messages.BAR_CONSOLE_NAME, BarPosition.BOTTOM_RIGHT, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadMemoryTab() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_MEMORY
        ).orElse(null);

        memoryPane = new MemoryPane(simulation);

        manageBarAddition("memory", memoryPane, icon, Messages.BAR_MEMORY_NAME, BarPosition.LEFT_TOP, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadFlow() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_FLOW
        ).orElse(null);


        FlowTable flow = FlowTable.createFlow(simulation);
        manageBarAddition("flow", flow, icon, Messages.BAR_FLOW_NAME, BarPosition.BOTTOM_RIGHT, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadLabels() {
        var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_LABELS).orElse(null);
        var pane = new LabelTable(this);

        manageBarAddition("labels", pane, icon, Messages.BAR_LABELS_NAME, BarPosition.RIGHT_BOTTOM, BarSnapshotViewModePane.INSTANCE, true);
    }

    private void loadCacheVisualizer() {
        var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CACHES).orElse(null);
        var memory = simulation.getMemory();
        if (memory instanceof Cache) {
            var visualizer = new CacheVisualizer(simulation);
            manageBarAddition("cache_visualizer", visualizer, icon, Messages.BAR_CACHES_NAME, BarPosition.LEFT_BOTTOM, BarSnapshotViewModePane.INSTANCE, true);
        }
    }


    private void manageBarAddition(String name, Node node, Image icon, String languageNode, BarPosition defaultPosition,
                                   BarSnapshotViewModePane defaultViewMode, boolean defaultEnable) {
        barMap.registerSnapshot(new BarPaneSnapshot(name, node, defaultPosition, defaultViewMode, defaultEnable, icon, languageNode));
    }


    @Override
    public String getLanguageNode() {
        return Messages.PROJECT_TAB_SIMULATION;
    }

    @Override
    public void onClose() {
        super.onClose();
        simulation.stop();
    }

    @Override
    public void populateHBox(HBox buttonsHBox) {
        buttonsHBox.getChildren().clear();

        var tooltip = new LanguageTooltip(Messages.ACTION_MIPS_SIMULATION_CYCLE_DELAY, LanguageTooltip.DEFAULT_DELAY);
        var delayHint = new Label("0ms");
        delayHint.setTooltip(tooltip);

        var bar = new Slider(0, 20, 0);
        bar.setPrefWidth(200);
        bar.setTooltip(new LanguageTooltip(Messages.ABOUT, LanguageTooltip.DEFAULT_DELAY));
        bar.valueProperty().addListener((obs, old, val) -> {
            double normalized = (Math.pow(2, val.doubleValue() / 5) - 1) / 15;
            int delay = (int) (normalized * 2000);
            simulation.setCycleDelay(delay);
            delayHint.setText(delay + "ms");
        });
        bar.setTooltip(tooltip);

        buttonsHBox.getChildren().addAll(delayHint, bar);
        buttonsHBox.getChildren().addAll(executionButtons.getNodes());
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }
}
