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

package net.jamsimulator.jams.gui.sidebar;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.sidebar.event.SidebarChangeNodeEvent;

import java.lang.reflect.Method;

/**
 * Represents a pane at the side of a window. This pane is handled by one or two {@link Sidebar}s
 * that uses it as a holder for their {@link SidePaneNode}s.
 */
public class SidePane extends SplitPane implements EventBroadcast {

	private SidePaneNode top, bottom;

	private final SimpleEventBroadcast broadcast;

	private final SplitPane parent;
	private final boolean left;

	private double dividerPosition, splitPaneDividerPosition;

	/**
	 * Creates the side pane.
	 *
	 * @param parent the parent pane.
	 * @param left   whether the side pane should be placed at the right or left side.
	 */
	public SidePane(SplitPane parent, boolean left) {
		SplitPane.setResizableWithParent(this, false);
		this.parent = parent;
		this.left = left;

		this.broadcast = new SimpleEventBroadcast();

		top = null;
		bottom = null;

		dividerPosition = 0.5;
		splitPaneDividerPosition = left ? 0.25 : 0.75;

		setOrientation(Orientation.VERTICAL);
	}

	/**
	 * Returns the top {@link SidePaneNode} or null.
	 *
	 * @return the top {@link SidePaneNode} or null.
	 */
	public SidePaneNode getTop() {
		return top;
	}

	/**
	 * Returns the bottom {@link SidePaneNode} or null.
	 *
	 * @return the bottom {@link SidePaneNode} or null.
	 */
	public SidePaneNode getBottom() {
		return bottom;
	}

	/**
	 * Sets the given {@link SidePaneNode} into the top cell of this side pane.
	 *
	 * @param top the {@link SidePaneNode}.
	 */
	void setTop(SidePaneNode top) {
		callEvent(new SidebarChangeNodeEvent(this, true, this.top, top));

		if (this.top != null) {
			if (bottom != null) storeDividerPosition();
			getItems().remove(this.top);
		}

		this.top = top;

		if (getItems().isEmpty() && top != null) {
			addToParent();
		}

		if (top != null) {
			getItems().add(0, top);
			if (bottom != null) setDividerPosition(0, dividerPosition);
		}

		removeFromParentIfEmpty();
	}

	/**
	 * Sets the given {@link SidePaneNode} into the bottom cell of this side pane.
	 *
	 * @param bottom the {@link SidePaneNode}.
	 */
	void setBottom(SidePaneNode bottom) {
		callEvent(new SidebarChangeNodeEvent(this, false, this.bottom, bottom));

		if (this.bottom != null) {
			if (top != null) storeDividerPosition();
			getItems().remove(this.bottom);
		}

		this.bottom = bottom;

		if (getItems().isEmpty() && bottom != null) {
			addToParent();
		}

		if (bottom != null) {
			getItems().add(top == null ? 0 : 1, bottom);
			if (top != null) setDividerPosition(0, dividerPosition);
		}
		removeFromParentIfEmpty();
	}

	/**
	 * Saves the divider position.
	 */
	private void storeDividerPosition() {
		dividerPosition = getDividerPositions()[0];
	}

	/**
	 * Adds this side pane into the split pane.
	 */
	private void addToParent() {
		if (left && parent.getItems().size() > 1) {
			double right = parent.getDividerPositions()[0];
			parent.getItems().add(0, this);
			parent.setDividerPosition(0, splitPaneDividerPosition);
			parent.setDividerPosition(1, right);
		} else {
			parent.getItems().add(left ? 0 : parent.getItems().size(), this);
			parent.setDividerPosition(getSplitPaneDividerIndex(), splitPaneDividerPosition);
		}
	}

	/**
	 * Removes this side pane from the split pane if this side pane is empty.
	 */
	private void removeFromParentIfEmpty() {
		if (getItems().isEmpty()) {
			splitPaneDividerPosition = parent.getDividerPositions()[getSplitPaneDividerIndex()];

			if (left && parent.getItems().size() > 2) {
				double right = parent.getDividerPositions()[1];
				parent.getItems().remove(this);
				parent.setDividerPosition(0, right);
			} else {
				parent.getItems().remove(this);
			}
		}
	}

	/**
	 * Returns the index of this side pane inside the parent.
	 *
	 * @return the index.
	 */
	private int getPaneIndex() {
		return left ? 0 : parent.getItems().size();
	}

	/**
	 * Returns the divider index of this pane inside the parent.
	 *
	 * @return the index.
	 */
	private int getSplitPaneDividerIndex() {
		return left ? 0 : parent.getItems().size() - 2;
	}

	//region EVENTS

	@Override
	public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
		return broadcast.registerListener(instance, method, useWeakReferences);
	}

	@Override
	public int registerListeners(Object instance, boolean useWeakReferences) {
		return broadcast.registerListeners(instance, useWeakReferences);
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		return broadcast.unregisterListener(instance, method);
	}

	@Override
	public int unregisterListeners(Object instance) {
		return broadcast.unregisterListeners(instance);
	}

	@Override
	public <T extends Event> T callEvent(T event) {
		return broadcast.callEvent(event, this);
	}

	//endregion
}
