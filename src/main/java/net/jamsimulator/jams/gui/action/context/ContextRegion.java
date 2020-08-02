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

package net.jamsimulator.jams.gui.action.context;

import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * Represents a region inside a {@link javafx.scene.control.Menu} or {@link javafx.scene.control.ContextMenu}
 * that handles {@link net.jamsimulator.jams.gui.action.Action}s.
 */
public class ContextRegion implements Comparable<ContextRegion> {

	public static final ContextRegion EMPTY = new ContextRegion("empty", null, Integer.MAX_VALUE);

	private final String name;
	private final ContextSubmenu submenu;

	private final int priority;

	/**
	 * Creates the context region.
	 *
	 * @param name     the name.
	 * @param submenu  the {@link ContextSubmenu} or null.
	 * @param priority the priority of the region.
	 */
	public ContextRegion(String name, ContextSubmenu submenu, int priority) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
		this.submenu = submenu;
		this.priority = priority;
	}

	/**
	 * Returns the name of this region.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link ContextSubmenu} of this region, if present.
	 *
	 * @return the {@link ContextSubmenu}, if present.
	 */
	public Optional<ContextSubmenu> getSubmenu() {
		return Optional.ofNullable(submenu);
	}


	/**
	 * Returns the priority of this region.
	 * Regions with higher priority will be shown first.
	 *
	 * @return the priority.
	 */
	public int getPriority() {
		return priority;
	}

	@Override
	public int compareTo(ContextRegion o) {
		return priority - o.priority;
	}
}
