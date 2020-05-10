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

package net.jamsimulator.jams.gui.action.defaults.explorerelement.mips.filestoassemble;

import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.folder.FolderActionRegions;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleDisplay;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleDisplayElement;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.mips.MipsFilesToAssemble;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.util.List;

public class MipsFilesToAssembleActionRemove extends ContextAction {


	public static final String NAME = "MIPS_FILES_TO_ASSEMBLE_REMOVE";
	public static final KeyCombination DEFAULT_COMBINATION = null;

	public MipsFilesToAssembleActionRemove() {
		super(NAME, RegionTags.MIPS_FILE_TO_ASSEMBLE, Messages.ACTION_MIPS_FILES_TO_ASSEMBLE_REMOVE,
				DEFAULT_COMBINATION, FolderActionRegions.ASSEMBLER, null);
	}

	@Override
	public void run(Node node) {
		if (!(node instanceof FilesToAssembleDisplayElement)) return;
		Explorer explorer = ((FilesToAssembleDisplayElement) node).getExplorer();
		if (!(explorer instanceof FilesToAssembleDisplay)) return;

		List<ExplorerElement> elements = explorer.getSelectedElements();
		if (elements.isEmpty()) return;

		ProjectTab tab = JamsApplication.getProjectsTabPane().getFocusedProject().orElse(null);
		if (tab == null) return;
		MipsProject project = tab.getProject();
		MipsFilesToAssemble files = project.getData().getFilesToAssemble();

		for (ExplorerElement element : elements) {
			files.removeFile(((FilesToAssembleDisplayElement) element).getFile());
		}
	}

	@Override
	public boolean supportsExplorerState(Explorer explorer) {
		if (!(explorer instanceof FilesToAssembleDisplay)) return false;
		List<ExplorerElement> elements = explorer.getSelectedElements();
		if (elements.isEmpty()) return false;
		return elements.stream().allMatch(target -> target instanceof FilesToAssembleDisplayElement);
	}
}
