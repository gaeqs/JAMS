package net.jamsimulator.jams.gui.project;

import javafx.scene.control.TabPane;
import net.jamsimulator.jams.Jams;
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
				Jams.getAssemblerBuilderManager().get("MIPS32").get(),
				Jams.getMemoryBuilderManager().get("MIPS32").get(),
				Jams.getRegistersBuilderManager().get("MIPS32").get(),
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true));
		if (!openProject(project)) System.err.println("ERROR WHILE OPENING DEBUG PROJECT!");
	}
}
