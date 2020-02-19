package net.jamsimulator.jams.gui.sidebar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.font.FontLoader;


public class SidebarButton extends Button {


	public SidebarButton(String name, boolean left, EventHandler<ActionEvent> action) {
		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");
		setOnAction(action);

		Label label = new Label(name);

		label.setPadding(new Insets(2, 0, 2, 0));
		Group group = new Group(label);

		setGraphic(group);
	}
}
