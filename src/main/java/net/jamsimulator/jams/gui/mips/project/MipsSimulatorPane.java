package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.mips.simulator.SimulatorCentralPane;
import net.jamsimulator.jams.gui.mips.simulator.register.RegistersTable;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.util.HashSet;
import java.util.Set;

public class MipsSimulatorPane extends WorkingPane {

	protected MipsProject project;
	protected Simulation<?> simulation;
	protected TabPane registersTabs;

	public MipsSimulatorPane(Tab parent, ProjectTab projectTab, MipsProject project, Simulation<?> simulation, Assembler assembler) {
		super(parent, projectTab, new SimulatorCentralPane(simulation, assembler.getOriginals()), false);
		this.project = project;
		this.simulation = simulation;

		init();

		SplitPane.setResizableWithParent(center, true);

		loadRegisterTabs();
	}

	public MipsProject getProject() {
		return project;
	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	private void loadRegisterTabs() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, 1024, 1024).orElse(null);

		registersTabs = new TabPane();


		Set<Register> general = new HashSet<>(simulation.getRegisterSet().getGeneralRegisters());
		general.add(simulation.getRegisterSet().getProgramCounter());

		registersTabs.getTabs().add(new Tab("General", new RegistersTable(general, false)));
		registersTabs.getTabs().add(new Tab("COP0", new RegistersTable(simulation.getRegisterSet().getCoprocessor0Registers(), false)));
		registersTabs.getTabs().add(new Tab("COP1", new RegistersTable(simulation.getRegisterSet().getCoprocessor1Registers(), true)));

		topRightSidebar.addNode("Registers", registersTabs, explorerIcon, null);
	}

	@Override
	public String getLanguageNode() {
		return Messages.PROJECT_TAB_SIMULATION;
	}
}
