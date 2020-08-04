package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.bar.BarType;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.flow.FlowTable;
import net.jamsimulator.jams.gui.mips.simulator.MIPSSimulationCentralPane;
import net.jamsimulator.jams.gui.mips.simulator.memory.MemoryPane;
import net.jamsimulator.jams.gui.mips.simulator.register.RegistersTable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.gui.util.ScalableNode;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.util.HashSet;
import java.util.Set;

public class MIPSSimulationPane extends WorkingPane implements ActionRegion {

	protected MIPSProject project;
	protected Simulation<?> simulation;
	protected TabPane registersTabs;

	public MIPSSimulationPane(Tab parent, ProjectTab projectTab, MIPSProject project, Simulation<?> simulation) {
		super(parent, projectTab, new MIPSSimulationCentralPane(simulation), new HashSet<>(), false);
		this.project = project;
		this.simulation = simulation;

		loadRegisterTabs();
		loadConsole();
		loadMemoryTab();
		loadFlow();

		init();

		SplitPane.setResizableWithParent(center, true);
	}

	public MIPSProject getProject() {
		return project;
	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	private void loadRegisterTabs() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_REGISTERS,
				Icons.SIMULATION_REGISTERS_PATH, 1024, 1024).orElse(null);

		registersTabs = new TabPane();


		Set<Register> general = new HashSet<>(simulation.getRegisters().getGeneralRegisters());
		general.add(simulation.getRegisters().getProgramCounter());

		registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_GENERAL, new RegistersTable(simulation, general, false)));
		registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP0, new RegistersTable(simulation, simulation.getRegisters().getCoprocessor0Registers(), false)));
		registersTabs.getTabs().add(new LanguageTab(Messages.REGISTERS_COP1, new RegistersTable(simulation, simulation.getRegisters().getCoprocessor1Registers(), true)));

		registersTabs.getTabs().forEach(tab -> tab.setClosable(false));

		paneSnapshots.add(new PaneSnapshot("Registers", BarType.TOP_RIGHT, registersTabs, icon, Messages.BAR_REGISTERS_NAME));
	}

	private void loadConsole() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CONSOLE,
				Icons.SIMULATION_CONSOLE_PATH, 1024, 1024).orElse(null);
		paneSnapshots.add(new PaneSnapshot("Console", BarType.BOTTOM, simulation.getConsole(), icon, Messages.BAR_CONSOLE_NAME));
	}

	private void loadMemoryTab() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_MEMORY,
				Icons.SIMULATION_MEMORY_PATH, 1024, 1024).orElse(null);

		MemoryPane pane = new MemoryPane(simulation);

		paneSnapshots.add(new PaneSnapshot("Memory", BarType.TOP_LEFT, pane, icon, Messages.BAR_MEMORY_NAME));
	}

	private void loadFlow() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_FLOW,
				Icons.SIMULATION_FLOW_PATH, 1024, 1024).orElse(null);


		Slider slider = new Slider(10, 100, 40);

		ScrollPane scroll = new ScrollPane();
		FlowTable flow = FlowTable.createFlow(simulation.getArchitecture(), simulation, scroll, slider);
		scroll.setContent(new ScalableNode(flow, scroll));
		scroll.setFitToHeight(true);

		AnchorPane anchor = new AnchorPane(scroll, slider);
		AnchorUtils.setAnchor(scroll, 0, 20, 0, 0);
		AnchorUtils.setAnchor(slider, -1, 0, 2, 2);
		slider.setPrefHeight(20);

		paneSnapshots.add(new PaneSnapshot("Flow", BarType.BOTTOM_LEFT, anchor, icon, Messages.BAR_FLOW_NAME));
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
	}

	@Override
	public boolean supportsActionRegion(String region) {
		return RegionTags.MIPS_SIMULATION.equals(region);
	}
}
