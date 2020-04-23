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

package net.jamsimulator.jams.gui.project;

import javafx.scene.control.TabPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.project.MipsProject;
import net.jamsimulator.jams.project.Project;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the projects' tab pane.
 * Projects' tabs are stored here.
 */
public class ProjectsTabPane extends TabPane {

	/**
	 * Creates the projects' main pane.
	 */
	public ProjectsTabPane() {
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		generateDebugProject();
	}

	/**
	 * Returns all {@link ProjectTab projects} stored inside this tab pane.
	 *
	 * @return all {@link ProjectTab projects}.
	 */
	public Set<ProjectTab> getProjects() {
		return getTabs().stream().filter(target -> target instanceof ProjectTab)
				.map(target -> (ProjectTab) target).collect(Collectors.toSet());
	}

	/**
	 * Returns whether the given {@link Project} is open in this tab pane.
	 *
	 * @param project the given {@link Project}.
	 * @return whether the given {@link Project} is open in this tab pane.
	 */
	public boolean isProjectOpen(Project project) {
		return getTabs().stream().anyMatch(target -> target instanceof ProjectTab
				&& ((ProjectTab) target).getProject().equals(project));
	}

	/**
	 * Opens the given {@link Project}.
	 *
	 * @param project the {@link Project}.
	 * @return whether the project was successfully open.
	 */
	public boolean openProject(Project project) {
		if (isProjectOpen(project)) return false;
		if (!(project instanceof MipsProject)) return false;
		ProjectTab tab = new ProjectTab((MipsProject) project);
		getTabs().add(tab);
		return true;
	}


	private void generateDebugProject() {
		String folder = System.getProperty("user.home") + File.separator + "JAMSProject";
		File file = new File(folder);
		if (!file.exists()) file.mkdirs();

		MipsProject project = new MipsProject("TEST", file,
				SingleCycleArchitecture.INSTANCE,
				Jams.getAssemblerBuilderManager().get("MIPS32").get(),
				Jams.getMemoryBuilderManager().get("MIPS32").get(),
				Jams.getRegistersBuilderManager().get("MIPS32").get(),
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true));
		if (!openProject(project)) System.err.println("ERROR WHILE OPENING DEBUG PROJECT!");
	}
}
