package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import net.jamsimulator.jams.gui.main.WorkingPane;


public class SidebarButton extends ToggleButton {

	private Sidebar sidebar;

	private String name;
	private SidePaneNode node;

	public SidebarButton(Sidebar sidebar, String name, SidePaneNode node, boolean left) {
		this.sidebar = sidebar;

		this.name = name;
		this.node = node;

		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");

		Label label = new Label(name);
		Group group = new Group(label);

		setGraphic(group);
		setPrefWidth(WorkingPane.SIDEBAR_WIDTH);

		selectedProperty().addListener((obs, old, val) -> {
			if (old == val) return;
			if (val) {
				sidebar.select(this);
			} else if (sidebar.getSelected() == node) {
				sidebar.select(null);
			}

		});

	}

	public Sidebar getSidebar() {
		return sidebar;
	}

	public String getName() {
		return name;
	}

	public SidePaneNode getNode() {
		return node;
	}
}
