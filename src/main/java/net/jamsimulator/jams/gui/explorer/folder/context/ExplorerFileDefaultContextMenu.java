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

package net.jamsimulator.jams.gui.explorer.folder.context;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerContextMenuItem;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.context.option.*;
import net.jamsimulator.jams.gui.explorer.folder.context.option.newmenu.FileMenuItemNew;

import java.awt.*;

public class ExplorerFileDefaultContextMenu extends ContextMenu {

	public static final ExplorerFileDefaultContextMenu INSTANCE = new ExplorerFileDefaultContextMenu();

	private ExplorerElement element;

	private ExplorerFileDefaultContextMenu() {

		getItems().add(new FileMenuItemNew(this));

		getItems().add(new SeparatorMenuItem());

		getItems().add(new FileMenuItemCopy(this));
		getItems().add(new FileMenuItemPaste(this));

		getItems().add(new SeparatorMenuItem());

		getItems().add(new FileMenuItemDelete(this));

		getItems().add(new SeparatorMenuItem());

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			getItems().add(new FileMenuItemShowInFiles(this));
		}
	}

	public ExplorerElement getCurrentElement() {
		return element;
	}

	public void setCurrentExplorerElement(ExplorerElement element) {
		this.element = element;
		getItems().forEach(target -> {
			if (target instanceof ExplorerContextMenuItem)
				((ExplorerContextMenuItem) target).onElementChange(element);
		});
	}

}
