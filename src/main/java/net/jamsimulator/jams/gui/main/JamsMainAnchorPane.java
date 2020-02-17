package net.jamsimulator.jams.gui.main;

import javafx.event.Event;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;


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

		topMenuBar.getMenus().add(new Menu("TEST"));
		topMenuBar.setPrefHeight(31);
	}

	private void generateProjectsTabPane() {
		projectsTabPane = new TabPane();
		projectsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		getChildren().add(projectsTabPane);
		AnchorUtils.setAnchor(projectsTabPane, 31, 0, 0, 0);

		Tab testTab = new Tab("Test tab");
		testTab.setClosable(true);
		testTab.setContent(new AnchorPane());

		Tab two = new Tab("Test tab 2");
		two.setClosable(true);

		two.setOnCloseRequest(Event::consume);

		projectsTabPane.getTabs().add(testTab);
		projectsTabPane.getTabs().add(two);
	}

}