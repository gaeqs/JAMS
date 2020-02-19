package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class SidePane extends SplitPane {

	private Node top, bottom;

	private SplitPane parent;
	private boolean left;

	public SidePane(SplitPane parent, boolean left) {
		this.parent = parent;
		this.left = left;
		top = null;
		bottom = null;

		setOrientation(Orientation.VERTICAL);
	}

	public Node getTop() {
		return top;
	}

	void setTop(Node top) {
		if (this.top != null) getItems().remove(this.top);
		this.top = top;

		if (getItems().isEmpty() && top != null) {
			parent.getItems().add(left ? 0 : parent.getItems().size(), this);
		}

		if (top != null) getItems().add(0, top);

		if (getItems().isEmpty()) {
			parent.getItems().remove(this);
		}
	}

	public Node getBottom() {
		return bottom;
	}

	public void setBottom(Node bottom) {
		if (this.bottom != null) getItems().remove(this.bottom);
		this.bottom = bottom;

		if (getItems().isEmpty() && bottom != null) {
			parent.getItems().add(left ? 0 : parent.getItems().size(), this);
		}

		if (bottom != null) getItems().add(top == null ? 0 : 1, bottom);


		if (getItems().isEmpty()) {
			parent.getItems().remove(this);
		}
	}
}
