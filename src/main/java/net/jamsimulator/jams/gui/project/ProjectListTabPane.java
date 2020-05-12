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

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the projects' tab pane.
 * Projects' tabs are stored here.
 */
public class ProjectListTabPane extends TabPane {

	/**
	 * Creates the projects' main pane.
	 */
	public ProjectListTabPane() {
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		generateDebugProject();
	}

	/**
	 * Returns the focused {@link ProjectTab} of this tab pane, if present.
	 *
	 * @return the {@link ProjectTab}, if present.
	 */
	public Optional<ProjectTab> getFocusedProject() {
		Tab tab = selectionModelProperty().getValue().getSelectedItem();
		if (!(tab instanceof ProjectTab)) return Optional.empty();
		return Optional.of((ProjectTab) tab);
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

	public Optional<ProjectTab> getProjectTab(Project project) {
		return getTabs().stream()
				.filter(target -> target instanceof ProjectTab && ((ProjectTab) target).getProject().equals(project))
				.map(target -> (ProjectTab) target)
				.findAny();
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
		project.assignProjectTab(tab);
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
				Jams.getDirectiveSetManager().get("MIPS32").get(),
				Jams.getInstructionSetManager().get("MIPS32").get());
		if (!openProject(project)) System.err.println("ERROR WHILE OPENING DEBUG PROJECT!");
	}
}
