package net.jamsimulator.jams.gui.main;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.project.MipsProject;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.io.File;

/**
 * This is the main pane of JAMS's main window.
 * It contains the top {@link MenuBar} and the projects {@link TabPane}.o
 */
public class MainAnchorPane extends AnchorPane {

	private MenuBar topMenuBar;
	private TabPane projectsTabPane;

	public MainAnchorPane() {
		generateTopMenuBar();
		generateProjectsTabPane();
	}


	private void generateTopMenuBar() {
		topMenuBar = new MainMenuBar();
		getChildren().add(topMenuBar);
		AnchorUtils.setAnchor(topMenuBar, -1, -1, 0, 0);
	}

	private void generateProjectsTabPane() {
		projectsTabPane = new TabPane();
		projectsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		getChildren().add(projectsTabPane);
		AnchorUtils.setAnchor(projectsTabPane, 23, 0, 0, 0);

		//region DEBUG. GENERATES EMPTY PROJECT
		String folder = System.getProperty("user.home") + File.separator + "JAMSProject";
		File file = new File(folder);
		if (!file.exists()) file.mkdirs();

		MipsProject project = new MipsProject("TEST", file,
				Jams.getAssemblerBuilderManager().get("MIPS32").get(),
				Jams.getMemoryBuilderManager().get("MIPS32").get(),
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true));

		Tab tab = new ProjectTab(project);
		tab.setClosable(true);
		projectsTabPane.getTabs().add(tab);
		//endregion
	}

	public MenuBar getTopMenuBar() {
		return topMenuBar;
	}

	public TabPane getProjectsTabPane() {
		return projectsTabPane;
	}
}