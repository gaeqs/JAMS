package net.jamsimulator.jams.gui.main;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.project.FolderProjectTab;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.project.FolderProject;
import net.jamsimulator.jams.utils.AnchorUtils;

import java.io.File;


public class JamsMainAnchorPane extends AnchorPane {

	private MenuBar topMenuBar;
	private TabPane projectsTabPane;

	public JamsMainAnchorPane() {
		generateTopMenuBar();
		generateProjectsTabPane();
	}


	private void generateTopMenuBar() {
		topMenuBar = new MenuBar();
		getChildren().add(topMenuBar);
		AnchorUtils.setAnchor(topMenuBar, -1, -1, 0, 0);

		Menu menu = new Menu("TEST");
		topMenuBar.getMenus().add(menu);
	}

	private void generateProjectsTabPane() {
		projectsTabPane = new TabPane();
		projectsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		getChildren().add(projectsTabPane);
		AnchorUtils.setAnchor(projectsTabPane, 22, 0, 0, 0);

		Tab testTab = new Tab("Test tab");
		testTab.setClosable(true);
		testTab.setContent(new AnchorPane());

		FolderProject project = new FolderProject("TEST", new File(System.getProperty("user.home")),
				Jams.getAssemblerBuilderManager().get("MIPS32").get(),
				Jams.getMemoryBuilderManager().get("MIPS32").get(),
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true));

		Tab two = new FolderProjectTab(project);
		two.setClosable(true);

		two.setOnCloseRequest(Event::consume);

		projectsTabPane.getTabs().add(testTab);
		projectsTabPane.getTabs().add(two);
	}

}