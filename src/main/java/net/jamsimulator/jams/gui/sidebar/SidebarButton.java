package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.main.WorkingPane;


/**
 * Represents a button inside a {@link Sidebar}.
 */
public class SidebarButton extends ToggleButton {

	private Sidebar sidebar;

	private String name;
	private SidePaneNode node;

	/**
	 * Creates a sidebar button.
	 *
	 * @param sidebar the sidebar handling this button.
	 * @param name    the name of this button.
	 * @param node    the node handled by this button.
	 * @param left    whether the sidebar is a left sidebar or a right one.
	 */
	public SidebarButton(Sidebar sidebar, String name, SidePaneNode node, boolean left) {
		this.sidebar = sidebar;
		this.name = name;
		this.node = node;

		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");

		Label label = new Label(name);
		Group group = new Group(label);

		ImageView imageView = new ImageView(new Image("gui/icon/project.png",
				WorkingPane.SIDEBAR_WIDTH, WorkingPane.SIDEBAR_WIDTH, true, false));

		VBox vBox = left ? new VBox(group, imageView) : new VBox(imageView, group);
		vBox.setSpacing(2);
		vBox.setAlignment(Pos.CENTER);

		setGraphic(vBox);
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

	/**
	 * Returns the {@link Sidebar} handling this button.
	 *
	 * @return the {@link Sidebar}.
	 */
	public Sidebar getSidebar() {
		return sidebar;
	}

	/**
	 * Returns the name of the button.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link SidePaneNode} handled by this button.
	 *
	 * @return the {@link SidePaneNode}.
	 */
	public SidePaneNode getNode() {
		return node;
	}
}
