package net.jamsimulator.jams.gui.mips.project;

import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.project.WorkingPane;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MipsProject;

public class MipsSimulatorPane extends WorkingPane {

	private MipsProject project;
	private Simulation<?> simulation;

	public MipsSimulatorPane(Tab parent, ProjectTab projectTab, MipsProject project, Simulation<?> simulation) {
		super(parent, projectTab, null, false);
		this.project = project;
		this.simulation = simulation;

		center = new AnchorPane();
		init();
	}

	public MipsProject getProject() {
		return project;
	}

	public Simulation<?> getSimulation() {
		return simulation;
	}

	@Override
	public String getLanguageNode() {
		return Messages.PROJECT_TAB_STRUCTURE;
	}

	@Override
	public void populateButtons(HBox buttons) {

	}
}
