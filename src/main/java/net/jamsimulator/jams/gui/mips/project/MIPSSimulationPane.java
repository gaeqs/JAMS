package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.bar.BarType;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
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
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MIPSProject;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MIPSSimulationPane extends WorkingPane implements ActionRegion {

    public static final String BAR_CONFIGURATION_NODE = "invisible.bar.simulation.";

    protected MIPSProject project;
    protected Simulation<?> simulation;
    protected TabPane registersTabs;

    protected final ExecutionButtons executionButtons;
    protected final InstructionViewerGroup instructionViewerGroup;
    protected MemoryPane memoryPane;


    public MIPSSimulationPane(Tab parent, ProjectTab projectTab, MIPSProject project, Simulation<?> simulation) {
        super(parent, projectTab, null, new HashSet<>(), false);
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

        loadRegisterTabs();
        loadConsole();
        loadMemoryTab();
        loadFlow();
        loadLabels();
        loadCacheVisualizer();

        init();

        SplitPane.setResizableWithParent(center, true);

        barMap.setOnPut((type, button) -> Jams.getMainConfiguration().set(BAR_CONFIGURATION_NODE + button.getName(), type));
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

        manageBarAddition("registers", registersTabs, icon, Messages.BAR_REGISTERS_NAME, BarType.TOP_RIGHT);
    }

    private void loadConsole() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CONSOLE
        ).orElse(null);

        manageBarAddition("console", simulation.getConsole(), icon, Messages.BAR_CONSOLE_NAME, BarType.BOTTOM);
    }

    private void loadMemoryTab() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_MEMORY
        ).orElse(null);

        memoryPane = new MemoryPane(simulation);

        manageBarAddition("memory", memoryPane, icon, Messages.BAR_MEMORY_NAME, BarType.TOP_LEFT);
    }

    private void loadFlow() {
        Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_FLOW
        ).orElse(null);


        FlowTable flow = FlowTable.createFlow(simulation);
        manageBarAddition("flow", flow, icon, Messages.BAR_FLOW_NAME, BarType.BOTTOM_LEFT);
    }

    private void loadLabels() {
        var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_LABELS).orElse(null);
        var pane = new LabelTable(this);

        manageBarAddition("labels", pane, icon, Messages.BAR_LABELS_NAME, BarType.BOTTOM_RIGHT);
    }

    private void loadCacheVisualizer() {
        var icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CACHES).orElse(null);
        var memory = simulation.getMemory();
        if (memory instanceof Cache) {
            var visualizer = new CacheVisualizer(simulation);
            manageBarAddition("cache_visualizer", visualizer, icon, Messages.BAR_CACHES_NAME, BarType.BOTTOM_LEFT);
        }
    }


    private void manageBarAddition(String name, Node node, Image icon, String languageNode, BarType bar) {
        Optional<BarType> optional = Jams.getMainConfiguration().getEnum(BarType.class, BAR_CONFIGURATION_NODE + name);
        if (optional.isPresent()) {
            bar = optional.get();
        } else {
            Jams.getMainConfiguration().set(BAR_CONFIGURATION_NODE + name, bar);
        }
        paneSnapshots.add(new PaneSnapshot(name, bar, node, icon, languageNode));
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
        buttonsHBox.getChildren().addAll(executionButtons.getNodes());
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }
}
