package net.jamsimulator.jams.gui.mips.simulator.instruction;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class InstructionViewerGroup {

	private final MIPSAssembledCodeViewer user, kernel;
	private final Tab userTab, kernelTab;
	private final TabPane pane;

	public InstructionViewerGroup(MIPSAssembledCodeViewer user) {
		this.user = user;
		this.kernel = null;
		this.userTab = null;
		this.kernelTab = null;
		this.pane = null;
	}

	public InstructionViewerGroup(MIPSAssembledCodeViewer user, MIPSAssembledCodeViewer kernel, Tab userTab, Tab kernelTab, TabPane pane) {
		this.user = user;
		this.kernel = kernel;
		this.userTab = userTab;
		this.kernelTab = kernelTab;
		this.pane = pane;
	}

	public MIPSAssembledCodeViewer getUser() {
		return user;
	}

	public MIPSAssembledCodeViewer getKernel() {
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
