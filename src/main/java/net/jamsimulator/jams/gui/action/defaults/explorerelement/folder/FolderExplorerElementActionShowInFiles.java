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

import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.ExplorerElementContextAction;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.language.Messages;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FolderExplorerElementActionShowInFiles extends ExplorerElementContextAction {


	public static final String NAME = "FOLDER_EXPLORER_ELEMENT_SHOW_IN_FILES";
	public static final KeyCombination DEFAULT_COMBINATION = null;

	public FolderExplorerElementActionShowInFiles() {
		super(NAME, RegionTags.FOLDER_EXPLORER_ELEMENT, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_SHOW_IN_FILES,
				DEFAULT_COMBINATION, "show");
	}

	@Override
	public void run(Node node) {
		if (!(node instanceof ExplorerElement)) return;
		Explorer explorer = ((ExplorerElement) node).getExplorer();
		if (!(explorer instanceof FolderExplorer)) return;
		if (explorer.getSelectedElements().size() != 1) return;

		ExplorerElement element = explorer.getSelectedElements().get(0);

		File folder;

		if (element instanceof ExplorerFile) {
			folder = ((ExplorerFile) element).getFile().getParentFile();
		} else if (element instanceof ExplorerFolder) {
			folder = ((ExplorerFolder) element).getFolder();
		} else {
			throw new IllegalStateException("Element is not a file or a folder!");
		}

		new Thread(() -> {
			try {
				Desktop.getDesktop().browse(folder.toURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public boolean supportsExplorerState(Explorer explorer) {
		return explorer instanceof FolderExplorer && explorer.getSelectedElements().size() == 1;
	}
}
