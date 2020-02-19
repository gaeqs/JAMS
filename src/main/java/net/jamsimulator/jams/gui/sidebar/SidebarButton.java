package net.jamsimulator.jams.gui.sidebar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.project.FolderProjectStructurePane;


public class SidebarButton extends Button {


	public SidebarButton(String name, boolean left, EventHandler<ActionEvent> action) {
		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");
		setOnAction(action);

		Label label = new Label(name);

		Group group = new Group(label);

		setGraphic(group);

		setPrefWidth(FolderProjectStructurePane.SIDEBAR_WIDTH);
	}
}
