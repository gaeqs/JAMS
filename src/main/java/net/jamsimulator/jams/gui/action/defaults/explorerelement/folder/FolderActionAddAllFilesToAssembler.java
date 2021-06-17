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
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.AssemblyFileType;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.FileEditorHolderHolder;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.FilesToAssemblerHolder;

import java.io.IOException;
import java.nio.file.Files;

public class FolderActionAddAllFilesToAssembler extends ContextAction {


    public static final String NAME = "FOLDER_EXPLORER_ELEMENT_ADD_ALL_FILES_TO_ASSEMBLER";
    public static final KeyCombination DEFAULT_COMBINATION = null;

    public FolderActionAddAllFilesToAssembler() {
        super(NAME, RegionTags.FOLDER_EXPLORER_ELEMENT, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_ADD_ALL_FILES_TO_ASSEMBLER,
                DEFAULT_COMBINATION, FolderActionRegions.ASSEMBLER, null, null);
    }

    @Override
    public void run(Object node) {
        if (!(node instanceof ExplorerElement)) return;
        var explorer = ((ExplorerElement) node).getExplorer();
        if (!(explorer instanceof FolderExplorer)) return;
        if (explorer.getSelectedElements().size() == 0) return;

        var tab = JamsApplication.getProjectsTabPane().getFocusedProject().orElse(null);
        if (tab == null) return;
        var project = tab.getProject();
        var data = project.getData();
        if (!(data instanceof FilesToAssemblerHolder)) return;
        var files = ((FilesToAssemblerHolder) data).getFilesToAssemble();
        var pane = tab.getProjectTabPane().getWorkingPane();
        if (!(pane instanceof FileEditorHolderHolder holder)) return;

        var element = explorer.getSelectedElements().get(0);
        if (!(element instanceof ExplorerFolder)) return;
        var folder = ((ExplorerFolder) element).getFolder();

        try {
            Files.walk(folder.toPath()).forEach(path -> {
                var file = path.toFile();
                if (file.isFile() && Jams.getFileTypeManager().getByFile(file).map(FileType::getName)
                        .orElse("").equals(AssemblyFileType.NAME)) {
                    files.addFile(file, holder.getFileEditorHolder(), true);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
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
        if (!(project.getData() instanceof FilesToAssemblerHolder)) return false;

        var selected = explorer.getSelectedElements();
        return selected.size() == 1 && selected.get(0) instanceof ExplorerFolder;
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
