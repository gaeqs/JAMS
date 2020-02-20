package net.jamsimulator.jams.gui.sidebar.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.SidePaneNode;

public class SidebarChangeNodeEvent extends Event {

	private SidePane sidePane;

	private boolean top;
	private SidePaneNode from, to;

	public SidebarChangeNodeEvent(SidePane sidePane, boolean top, SidePaneNode from, SidePaneNode to) {
		this.sidePane = sidePane;
		this.top = top;
		this.to = to;
		this.from = from;
	}

	public SidePane getSidePane() {
		return sidePane;
	}

	public boolean isTop() {
		return top;
	}

	public SidePaneNode getFrom() {
		return from;
	}

	public SidePaneNode getTo() {
		return to;
	}
}
