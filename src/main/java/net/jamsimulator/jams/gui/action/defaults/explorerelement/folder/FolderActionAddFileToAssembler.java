/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.action.defaults.explorerelement.folder;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.file.AssemblyFileType;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.holder.FileEditorHolderHolder;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.project.FilesToAssemblerHolder;

public class FolderActionAddFileToAssembler extends ContextAction {


    public static final String NAME = "FOLDER_EXPLORER_ELEMENT_ADD_FILE_TO_ASSEMBLER";
    public static final KeyCombination DEFAULT_COMBINATION = null;

    public FolderActionAddFileToAssembler(ResourceProvider provider) {
        super(provider, NAME, RegionTags.FOLDER_EXPLORER_ELEMENT, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_ADD_FILE_TO_ASSEMBLER,
                DEFAULT_COMBINATION, FolderActionRegions.ASSEMBLER, null, null);
    }

    @Override
    public void run(Object node) {
        if (!(node instanceof ExplorerElement)) return;
        var explorer = ((ExplorerElement) node).getExplorer();
        if (!(explorer instanceof FolderExplorer)) return;

        var elements = explorer.getSelectedElements();
        if (elements.isEmpty()) return;

        if (!elements.stream().allMatch(target -> target instanceof ExplorerFile
                && Manager.get(FileTypeManager.class).getByFile(((ExplorerFile) target).getFile())
                .map(FileType::getName).orElse("").equals(AssemblyFileType.NAME))) return;

        var tab = JamsApplication.getProjectsTabPane().getFocusedProject().orElse(null);
        if (tab == null) return;
        var project = tab.getProject();
        var data = project.getData();
        if (!(data instanceof FilesToAssemblerHolder)) return;
        var files = ((FilesToAssemblerHolder) data).getFilesToAssemble();
        var pane = tab.getProjectTabPane().getWorkingPane();
        if (!(pane instanceof FileEditorHolderHolder holder)) return;

        for (ExplorerElement element : elements) {
            files.addFile(((ExplorerFile) element).getFile(), holder.getFileEditorHolder(), true);
        }
    }

    @Override
    public void runFromMenu() {

    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        if (!(explorer instanceof FolderExplorer)) return false;

        var elements = explorer.getSelectedElements();
        if (elements.isEmpty()) return false;

        var tab = JamsApplication.getProjectsTabPane().getFocusedProject().orElse(null);
        if (tab == null) return false;
        var project = tab.getProject();
        var data = project.getData();
        if (!(data instanceof FilesToAssemblerHolder)) return false;

        var files = ((FilesToAssemblerHolder) data).getFilesToAssemble();

        boolean allPresent = true;
        for (ExplorerElement element : elements) {
            //Is not a file
            if (!(element instanceof ExplorerFile)) return false;
            //Is not an assembly file
            if (!Manager.get(FileTypeManager.class).getByFile(((ExplorerFile) element).getFile())
                    .map(FileType::getName).orElse("").equals(AssemblyFileType.NAME))
                return false;

            allPresent &= files.containsFile(((ExplorerFile) element).getFile());
        }

        return !allPresent;
    }

    @Override
    public boolean supportsTextEditorState(CodeFileEditor editor) {
        return false;
    }

    @Override
    public boolean supportsMainMenuState(MainMenuBar bar) {
        return false;
    }
}
