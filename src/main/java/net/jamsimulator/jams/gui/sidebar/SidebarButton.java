package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;


/**
 * Represents a button inside a {@link Sidebar}.
 */
public class SidebarButton extends ToggleButton {

	public static final int IMAGE_SIZE = 16;

	private Sidebar sidebar;

	private String name;
	private SidePaneNode node;
	private Image image;

	/**
	 * Creates a sidebar button.
	 *
	 * @param sidebar the sidebar handling this button.
	 * @param name    the name of this button.
	 * @param node    the node handled by this button.
	 * @param left    whether the sidebar is a left sidebar or a right one.
	 * @param icon    the icon of the button, or null.
	 */
	public SidebarButton(Sidebar sidebar, String name, SidePaneNode node, boolean left, Image icon, String languageNode) {
		this.sidebar = sidebar;
		this.name = name;
		this.node = node;

		getStyleClass().addAll("sidebar-button",
				left ? "sidebar-button-left" : "sidebar-button-right");

		Label label = languageNode == null ? new Label(name) : new LanguageLabel(languageNode);
		Group group = new Group(label);

		if (icon != null) {
			ImageView imageView = new ImageView(icon);
			VBox vBox = left ? new VBox(group, imageView) : new VBox(imageView, group);
			vBox.setSpacing(2);
			vBox.setAlignment(Pos.CENTER);
			setGraphic(vBox);
		} else {
			setGraphic(group);
		}

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
