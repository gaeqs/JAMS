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
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFile;
import net.jamsimulator.jams.gui.explorer.folder.ExplorerFolder;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.popup.NewFileWindow;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.file.FileTypeManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.io.File;

public class FolderActionNewFile extends ContextAction {


    public static final String NAME = "FOLDER_EXPLORER_ELEMENT_NEW_FILE";
    public static final KeyCombination DEFAULT_COMBINATION = null;

    public FolderActionNewFile(ResourceProvider provider) {
        super(provider,NAME, RegionTags.FOLDER_EXPLORER_ELEMENT, Messages.ACTION_FOLDER_EXPLORER_ELEMENT_NEW_FILE,
                DEFAULT_COMBINATION, FolderActionRegions.NEW_GENERAL, null,
                Manager.get(FileTypeManager.class).getUnknownType().getIcon());
    }

    @Override
    public boolean run(Object node) {
        if (!(node instanceof ExplorerElement)) return false;
        Explorer explorer = ((ExplorerElement) node).getExplorer();
        if (!(explorer instanceof FolderExplorer)) return false;
        if (explorer.getSelectedElements().size() != 1) return false;

        ExplorerElement element = explorer.getSelectedElements().get(0);

        File folder;

        if (element instanceof ExplorerFile) {
            folder = ((ExplorerFile) element).getFile().getParentFile();
        } else if (element instanceof ExplorerFolder) {
            folder = ((ExplorerFolder) element).getFolder();
        } else {
            throw new IllegalStateException("Element is not a file or a folder!");
        }

        NewFileWindow.open(folder);
        return true;
    }

    @Override
    public void runFromMenu() {

    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        return explorer instanceof FolderExplorer && explorer.getSelectedElements().size() == 1;
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
