package net.jamsimulator.jams.gui.sidebar.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.sidebar.SidePane;
import net.jamsimulator.jams.gui.sidebar.SidePaneNode;

/**
 * This event is called by a {@link SidePane} when a node is changed.
 * This event is called before the nodes are changed.
 */
public class SidebarChangeNodeEvent extends Event {

	private SidePane sidePane;

	private boolean top;
	private SidePaneNode from, to;

	/**
	 * Creates the event.
	 *
	 * @param sidePane the changed {@link SidePane}.
	 * @param top      whether the changed node is the top node.
	 * @param from     the node to be replaced.
	 * @param to       the new node.
	 */
	public SidebarChangeNodeEvent(SidePane sidePane, boolean top, SidePaneNode from, SidePaneNode to) {
		this.sidePane = sidePane;
		this.top = top;
		this.to = to;
		this.from = from;
	}

	/**
	 * Returns the {@link SidePane} that is changing its node.
	 *
	 * @return the {@link SidePane}.
	 */
	public SidePane getSidePane() {
		return sidePane;
	}

	/**
	 * Return whether the changed node is the top node.
	 *
	 * @return whether the changed node is the top node.
	 */
	public boolean isTop() {
		return top;
	}

	/**
	 * Returns the {@link SidePaneNode} to be replaced.
	 *
	 * @return the {@link SidePaneNode}.
	 */
	public SidePaneNode getFrom() {
		return from;
	}

	/**
	 * Returns the new {@link SidePaneNode}.
	 *
	 * @return the {@link SidePaneNode}.
	 */
	public SidePaneNode getTo() {
		return to;
	}
}
