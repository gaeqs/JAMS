/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.bar.sidebar.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.bar.sidebar.SidePane;
import net.jamsimulator.jams.gui.bar.sidebar.SidePaneNode;

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
