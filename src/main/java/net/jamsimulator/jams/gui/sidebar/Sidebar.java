package net.jamsimulator.jams.gui.sidebar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

	private boolean left;

	public Sidebar(boolean left) {
		this.left = left;
		getStyleClass().addAll("sidebar", left ? "sidebar-left" : "sidebar-right");
	}

	public boolean isLeft() {
		return left;
	}


	public void createSidebarButton(String name, EventHandler<ActionEvent> action) {
		getChildren().add(new SidebarButton(name, left, action));
	}
}
