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

package net.jamsimulator.jams.gui.action.defaults.general;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.bar.BarButton;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.mips.editor.MIPSFileEditor;
import net.jamsimulator.jams.gui.mips.project.MIPSSimulationPane;
import net.jamsimulator.jams.gui.mips.project.MipsStructurePane;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.util.log.SimpleLog;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.util.Optional;

public class GeneralActionCompile extends ContextAction {

	public static final String NAME = "GENERAL_COMPILE";
	public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN);

	public GeneralActionCompile() {
		super(NAME, RegionTags.GENERAL, Messages.ACTION_GENERAL_COMPILE, DEFAULT_COMBINATION, GeneralActionRegions.MIPS_PRIORITY, MainMenuRegion.MIPS,
				JamsApplication.getIconManager().getOrLoadSafe(Icons.PROJECT_ASSEMBLE, Icons.PROJECT_ASSEMBLE_PATH, 1024, 1024).orElse(null));
	}

	@Override
	public void run(Object node) {
		if (node instanceof MIPSFileEditor) {
			MIPSProject project = ((MIPSFileEditor) node).getProject().orElse(null);
			if (project == null) return;
			compileAndShow(project);
		} else {
			Optional<ProjectTab> optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
			if (!optionalProject.isPresent()) return;
			ProjectTab tab = optionalProject.get();
			if (tab.getProject() instanceof MIPSProject) {
				compileAndShow((MIPSProject) tab.getProject());
			}
		}
	}

	public static void compileAndShow(MIPSProject project) {
		ProjectTab tab = project.getProjectTab().orElse(null);
		if (tab == null) return;

		MipsStructurePane pane = (MipsStructurePane) tab.getProjectTabPane().getWorkingPane();
		pane.getFileDisplayHolder().saveAll(true);
		pane.getBarMap().searchButton("Log").ifPresent(BarButton::show);
		SimpleLog log = pane.getLog();

		try {
			Simulation<?> simulation = project.assemble(log);

			project.getProjectTab().ifPresent(projectTab -> projectTab.getProjectTabPane()
					.createProjectPane((t, pt) ->
							new MIPSSimulationPane(t, pt, project, simulation), true));


		} catch (Exception ex) {
			log.printErrorLn("ERROR:");
			log.printErrorLn(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private static String toHexFill(int i) {
		return addZeros(Integer.toHexString(i), 8);
	}

	private static String addZeros(String s, int to) {
		StringBuilder builder = new StringBuilder();
		int max = Math.max(0, to - s.length());

		for (int i = 0; i < max; i++) {
			builder.append("0");
		}

		return builder + s;
	}

	@Override
	public void runFromMenu() {
		Optional<ProjectTab> optionalProject = JamsApplication.getProjectsTabPane().getFocusedProject();
		if (!optionalProject.isPresent()) return;
		ProjectTab tab = optionalProject.get();
		if (tab.getProject() instanceof MIPSProject) {
			compileAndShow((MIPSProject) tab.getProject());
		}
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
		return optionalProject.isPresent();
	}
}
