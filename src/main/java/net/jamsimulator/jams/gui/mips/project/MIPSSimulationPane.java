package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.bar.BarType;
import net.jamsimulator.jams.gui.bar.PaneSnapshot;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.simulator.MIPSSimulationCentralPane;
import net.jamsimulator.jams.gui.mips.simulator.memory.MemoryTable;
import net.jamsimulator.jams.gui.mips.simulator.register.RegistersTable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.util.HashSet;
import java.util.Set;

public class MIPSSimulationPane extends WorkingPane {

	protected MipsProject project;
	protected Simulation<?> simulation;
	protected TabPane registersTabs;

	public MIPSSimulationPane(Tab parent, ProjectTab projectTab, MipsProject project, Simulation<?> simulation, Assembler assembler) {
		super(parent, projectTab, new MIPSSimulationCentralPane(simulation, assembler.getOriginals()), new HashSet<>(), false);
		this.project = project;
		this.simulation = simulation;

		loadRegisterTabs();
		loadConsole();
		loadMemoryTab();

		init();

		SplitPane.setResizableWithParent(center, true);
	}

	public MipsProject getProject() {
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

		paneSnapshots.add(new PaneSnapshot("Registers", BarType.TOP_RIGHT, registersTabs, icon, Messages.REGISTERS_NAME));
	}

	private void loadConsole() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_CONSOLE,
				Icons.SIMULATION_CONSOLE_PATH, 1024, 1024).orElse(null);
		paneSnapshots.add(new PaneSnapshot("Console", BarType.BOTTOM, simulation.getConsole(), icon, Messages.CONSOLE_NAME));
	}

	private void loadMemoryTab() {
		Image icon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_MEMORY,
				Icons.SIMULATION_MEMORY_PATH, 1024, 1024).orElse(null);

		MemoryTable table = new MemoryTable(simulation);

		paneSnapshots.add(new PaneSnapshot("Memory", BarType.TOP_LEFT, table, icon, Messages.SIMULATION_CONFIGURATION_MEMORY));
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
}
