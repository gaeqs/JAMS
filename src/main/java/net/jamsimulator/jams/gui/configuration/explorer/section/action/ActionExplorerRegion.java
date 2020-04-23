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

package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import net.jamsimulator.jams.gui.explorer.*;
import net.jamsimulator.jams.manager.ActionManager;

import java.util.Comparator;

/**
 * Represents an {@link net.jamsimulator.jams.gui.action.Action} region inside the explorer.
 */
public class ActionExplorerRegion extends ExplorerSection {

	/**
	 * Creates the explorer section.
	 *
	 * @param explorer the {@link Explorer} of this section.
	 * @param parent   the {@link ExplorerSection} containing this section. This may be null.
	 * @param region   the region.
	 */
	public ActionExplorerRegion(ActionsExplorer explorer, ExplorerSection parent, String region) {
		super(explorer, parent, region, 1, Comparator.comparing(ExplorerElement::getName));
		((ExplorerSectionLanguageRepresentation) representation).setLanguageNode(ActionManager.LANGUAGE_REGION_NODE_PREFIX + region);
	}

	/**
	 * Disposes this element, removing all listeners.
	 * This should be called when this element is not longer needed.
	 */
	public void dispose() {
		((ExplorerSectionLanguageRepresentation) representation).dispose();
	}


	@Override
	protected ExplorerSectionRepresentation loadRepresentation() {
		return new ExplorerSectionLanguageRepresentation(this, hierarchyLevel, null);
	}

}
