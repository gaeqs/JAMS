package net.jamsimulator.jams.gui.sidebar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;
import net.jamsimulator.jams.gui.font.FontLoader;


public class SidebarButton extends Button {


	public SidebarButton(String name, boolean left, EventHandler<ActionEvent> action) {
		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");
		setOnAction(action);
		setPrefHeight(100);

		Label label = new Label("AAAAAAAAAAAAAAAAAAAAA");
		label.setFont(new Font(label.getFont().getName(), 12));
		label.setBorder(Border.EMPTY);


		setGraphic(new Group(label));
	}
}
