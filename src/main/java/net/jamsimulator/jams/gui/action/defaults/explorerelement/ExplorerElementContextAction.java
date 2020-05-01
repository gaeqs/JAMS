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

package net.jamsimulator.jams.gui.action.defaults.explorerelement;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.explorer.Explorer;

/**
 * Represents a explorer element action that can be shown in a {@link javafx.scene.control.ContextMenu}.
 */
public abstract class ExplorerElementContextAction extends Action {

	private final String menu;

	/**
	 * Creates the context action.
	 *
	 * @param name               the name of the action. This name must be unique.
	 * @param regionTag          the region tag of this action. This action will only interact on regions that support this tag.
	 * @param languageNode       the language node of this action.
	 * @param defaultCombination the default combination of keys that a user needs to press to execute this action.
	 * @param menu               the menu where this action should be displayed. Use points '.' to separate several submenus.
	 */
	public ExplorerElementContextAction(String name, String regionTag,
										String languageNode, KeyCombination defaultCombination, String menu) {
		super(name, regionTag, languageNode, defaultCombination);
		this.menu = menu;
	}

	/**
	 * Returns the menu where this action should be displayed.
	 * If there are several submenus, this string separates them using a point '.'.
	 * <p>
	 * If there's no submenus, this action will be displayed in the main context menu.
	 *
	 * @return the menu.
	 */
	public String getMenu() {
		return menu;
	}

	/**
	 * Returns whether this action can be shown in the given {@link Explorer}.
	 *
	 * @param explorer the {@link Explorer}.
	 */
	public abstract boolean supportsExplorerState(Explorer explorer);
}
