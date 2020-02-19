package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class SidePane extends VBox {

	private Node top, bottom;

	public SidePane() {
		top = null;
		bottom = null;
	}

	public Node getTop() {
		return top;
	}

	void setTop(Node top) {
		if (this.top != null) getChildren().remove(this.top);
		this.top = top;
		getChildren().add(0, top);
	}

	public Node getBottom() {
		return bottom;
	}

	public void setBottom(Node bottom) {
		if (this.bottom != null) getChildren().remove(this.bottom);
		this.bottom = bottom;
		getChildren().add(top == null ? 0 : 1, bottom);
	}
}
