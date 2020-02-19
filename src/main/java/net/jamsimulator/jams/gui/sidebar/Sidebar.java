package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

	private boolean left;
	private boolean top;

	private SidePane sidePane;

	public Sidebar(boolean left, boolean top, SidePane sidePane) {
		this.left = left;
		this.top = top;
		this.sidePane = sidePane;
		getStyleClass().addAll("sidebar", left ? "sidebar-left" : "sidebar-right");

		if (!top) setAlignment(Pos.BOTTOM_LEFT);
	}

	public SidePane getSidePane() {
		return sidePane;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isTop() {
		return top;
	}


	public boolean containsNode(String name) {
		return getChildren().stream().anyMatch(target -> target instanceof SidebarButton
				&& ((SidebarButton) target).getName().equals(name));
	}

	public boolean addNode(String name, Node node) {
		if (containsNode(name)) return false;
		SidebarButton button = new SidebarButton(this, name, node, left);

		getChildren().add(button);

		return true;
	}

	Node getSelected() {
		return top ? sidePane.getTop() : sidePane.getBottom();
	}

	void deselectExcept(SidebarButton except) {
		for (Node child : getChildren()) {
			if (except == child) continue;
			if (child instanceof SidebarButton)
				((SidebarButton) child).setSelected(false);
		}
	}

	void select(SidebarButton button) {
		if (button != null) deselectExcept(button);
		if (top) {
			sidePane.setTop(button == null ? null : button.getNode());
		} else {
			sidePane.setBottom(button == null ? null : button.getNode());
		}
	}
}
