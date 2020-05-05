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

package net.jamsimulator.jams.gui.action.defaults.explorerelement.folder;

import net.jamsimulator.jams.gui.action.context.ContextRegion;
import net.jamsimulator.jams.gui.action.context.ContextSubmenu;
import net.jamsimulator.jams.language.Messages;

public class FolderActionRegions {

	public static final ContextRegion NEW = new ContextRegion("new", null, 0);
	public static final ContextRegion CLIPBOARD = new ContextRegion("clipboard", null, 1);
	public static final ContextRegion SHOW = new ContextRegion("show", null, 999);

	public static final ContextRegion ASSEMBLER = new ContextRegion("show", null, 2);

	public static final ContextSubmenu NEW_SUBMENU = new ContextSubmenu("new", NEW, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_NEW);
	public static final ContextRegion NEW_GENERAL = new ContextRegion("general", NEW_SUBMENU, 0);

}
