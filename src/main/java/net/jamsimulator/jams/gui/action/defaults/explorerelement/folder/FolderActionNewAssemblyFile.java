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

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.popup.NewAssemblyFileWindow;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.Project;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.io.File;
import java.util.Optional;

public class FolderActionNewAssemblyFile extends ContextAction {


	public static final String NAME = "FOLDER_EXPLORER_ELEMENT_NEW_ASSEMBLY_FILE";
	public static final KeyCombination DEFAULT_COMBINATION = null;

	public FolderActionNewAssemblyFile() {
		super(NAME, RegionTags.FOLDER_EXPLORER_ELEMENT, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_NEW_ASSEMBLY_FILE,
				DEFAULT_COMBINATION, FolderActionRegions.NEW_GENERAL,
				Jams.getFileTypeManager().getByExtension("asm").map(FileType::getIcon).orElse(
						Jams.getFileTypeManager().getUnknownType().getIcon()));
	}

	@Override
	public void run(Object node) {
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

		Optional<Project> optional = JamsApplication.getProjectsTabPane().getFocusedProject().map(ProjectTab::getProject);
		if (!optional.isPresent() || !(optional.get() instanceof MipsProject)) {
			NewAssemblyFileWindow.open(folder, null);
		} else {
			NewAssemblyFileWindow.open(folder, (MipsProject) optional.get());
		}
	}

	@Override
	public boolean supportsExplorerState(Explorer explorer) {
		return explorer instanceof FolderExplorer && explorer.getSelectedElements().size() == 1;
	}

	@Override
	public boolean supportsTextEditorState(CodeFileEditor editor) {
		return false;
	}
}
