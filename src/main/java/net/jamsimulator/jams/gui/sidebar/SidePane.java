package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.sidebar.event.SidebarChangeNodeEvent;

import java.lang.reflect.Method;

public class SidePane extends SplitPane implements EventBroadcast {

	private SidePaneNode top, bottom;

	private SimpleEventBroadcast broadcast;

	private SplitPane parent;
	private boolean left;

	private double dividerPosition, splitPaneDividerPosition;

	public SidePane(SplitPane parent, boolean left) {
		this.parent = parent;
		this.left = left;

		this.broadcast = new SimpleEventBroadcast();

		top = null;
		bottom = null;

		dividerPosition = 0.5;
		splitPaneDividerPosition = 0.2;

		setOrientation(Orientation.VERTICAL);
	}

	public SidePaneNode getTop() {
		return top;
	}

	void setTop(SidePaneNode top) {
		if (this.top != null) {
			if (bottom != null) dividerPosition = getDividerPositions()[0];
			getItems().remove(this.top);
		}

		callEvent(new SidebarChangeNodeEvent(this, true, this.top, top));

		this.top = top;

		if (getItems().isEmpty() && top != null) {
			parent.getItems().add(left ? 0 : parent.getItems().size(), this);
			parent.setDividerPosition(left ? 0 : parent.getItems().size() - 2,
					splitPaneDividerPosition);
		}

		if (top != null) {
			getItems().add(0, top);
			if (bottom != null) setDividerPosition(0, dividerPosition);
		}

		if (getItems().isEmpty()) {
			splitPaneDividerPosition = parent.getDividerPositions()
					[left ? 0 : parent.getItems().size() - 2];
			parent.getItems().remove(this);
		}
	}

	public SidePaneNode getBottom() {
		return bottom;
	}

	public void setBottom(SidePaneNode bottom) {
		if (this.bottom != null) {
			if (top != null) dividerPosition = getDividerPositions()[0];
			getItems().remove(this.bottom);
		}
		this.bottom = bottom;

		callEvent(new SidebarChangeNodeEvent(this, false, this.bottom, bottom));

		if (getItems().isEmpty() && bottom != null) {
			parent.getItems().add(left ? 0 : parent.getItems().size(), this);
			parent.setDividerPosition(left ? 0 : parent.getItems().size() - 2,
					splitPaneDividerPosition);
		}

		if (bottom != null) {
			getItems().add(top == null ? 0 : 1, bottom);
			if (top != null) setDividerPosition(0, dividerPosition);
		}


		if (getItems().isEmpty()) {
			splitPaneDividerPosition = parent.getDividerPositions()
					[left ? 0 : parent.getItems().size() - 2];
			parent.getItems().remove(this);
		}
	}

	//region EVENTS

	@Override
	public boolean registerListener(Object instance, Method method) {
		return broadcast.registerListener(instance, method);
	}

	@Override
	public int registerListeners(Object instance) {
		return broadcast.registerListeners(instance);
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		return broadcast.unregisterListener(instance, method);
	}

	@Override
	public int unregisterListeners(Object instance) {
		return broadcast.registerListeners(instance);
	}

	@Override
	public <T extends Event> T callEvent(T event) {
		return broadcast.callEvent(event, this);
	}

	//endregion
}
