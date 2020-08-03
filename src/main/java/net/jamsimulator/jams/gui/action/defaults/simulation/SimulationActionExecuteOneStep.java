/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.action.defaults.simulation;

import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class SimulationActionExecuteOneStep extends ContextAction {

	public static final String NAME = "ACTION_MIPS_SIMULATION_EXECUTE_ONE_STEP";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

	public SimulationActionExecuteOneStep() {
		super(NAME, RegionTags.MIPS_SIMULATION, Messages.ACTION_MIPS_SIMULATION_EXECUTE_ONE_STEP, DEFAULT_COMBINATION, SImulationActionRegions.MIPS, MainMenuRegion.MIPS,
				JamsApplication.getIconManager().getOrLoadSafe(Icons.SIMULATION_PLAY_ONE, Icons.SIMULATION_PLAY_ONE_PATH, 1024, 1024).orElse(null));
	}

	@Override
	public void run(Object node) {
		Optional<ProjectTab> optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
		if (!optionalProject.isPresent()) return;
		ProjectTab projectTab = optionalProject.get();
		Tab tab = projectTab.getProjectTabPane().getSelectionModel().getSelectedItem();
		if (tab == null || !(tab.getContent() instanceof MIPSSimulationPane)) return;

		MIPSSimulationPane pane = (MIPSSimulationPane) tab.getContent();
		Simulation<?> simulation = pane.getSimulation();

		if (simulation.isRunning()) return;

		simulation.nextStep();
	}


	@Override
	public void runFromMenu() {
		run(null);
	}

	@Override
	public boolean supportsExplorerState(Explorer explorer) {
		return false;
	}

	@Override
	public boolean supportsTextEditorState(CodeFileEditor editor) {
		return false;
	}

	@Override
	public boolean supportsMainMenuState(MainMenuBar bar) {
		Optional<ProjectTab> optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
		if (!optionalProject.isPresent()) return false;
		ProjectTab projectTab = optionalProject.get();
		Tab tab = projectTab.getProjectTabPane().getSelectionModel().getSelectedItem();
		return tab != null && tab.getContent() instanceof MIPSSimulationPane && !((MIPSSimulationPane) tab.getContent()).getSimulation().isRunning();
	}
}
