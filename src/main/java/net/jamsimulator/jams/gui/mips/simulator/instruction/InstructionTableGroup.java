package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class InstructionTableGroup {

	private final InstructionsTable user, kernel;
	private final Tab userTab, kernelTab;
	private final TabPane pane;

	public InstructionTableGroup(InstructionsTable user) {
		this.user = user;
		this.kernel = null;
		this.userTab = null;
		this.kernelTab = null;
		this.pane = null;
	}

	public InstructionTableGroup(InstructionsTable user, InstructionsTable kernel, Tab userTab, Tab kernelTab, TabPane pane) {
		this.user = user;
		this.kernel = kernel;
		this.userTab = userTab;
		this.kernelTab = kernelTab;
		this.pane = pane;
	}

	public InstructionsTable getUser() {
		return user;
	}

	public InstructionsTable getKernel() {
		return kernel;
	}

	public boolean selectUser() {
		if (pane == null) return false;
		pane.getSelectionModel().select(userTab);
		return true;
	}

	public boolean selectKernel() {
		if (pane == null) return false;
		pane.getSelectionModel().select(kernelTab);
		return true;
	}
}
