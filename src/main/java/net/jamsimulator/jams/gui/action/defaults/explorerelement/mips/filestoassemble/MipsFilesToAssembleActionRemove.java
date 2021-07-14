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

package net.jamsimulator.jams.gui.action.defaults.explorerelement.mips.filestoassemble;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.folder.FolderActionRegions;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.mips.sidebar.FilesToAssembleSidebarElement;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.FilesToAssemblerHolder;

public class MipsFilesToAssembleActionRemove extends ContextAction {


    public static final String NAME = "MIPS_FILES_TO_ASSEMBLE_REMOVE";
    public static final KeyCombination DEFAULT_COMBINATION = null;

    public MipsFilesToAssembleActionRemove() {
        super(NAME, RegionTags.MIPS_FILE_TO_ASSEMBLE, Messages.ACTION_MIPS_FILES_TO_ASSEMBLE_REMOVE,
                DEFAULT_COMBINATION, FolderActionRegions.ASSEMBLER, null, null);
    }

    @Override
    public void run(Object node) {
        if (!(node instanceof FilesToAssembleSidebarElement element)) return;
        var data = element.getDisplay().getProject().getData();
        if (!(data instanceof FilesToAssemblerHolder holder)) return;
        var files = holder.getFilesToAssemble();
        files.removeFile(element.getItem());
    }

    @Override
    public void runFromMenu() {

    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        return false;
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
