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

package net.jamsimulator.jams.gui.action.defaults.texteditor;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.main.MainMenuBar;
import net.jamsimulator.jams.gui.util.CodeFileEditorUtils;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

public class TextEditorActionDuplicateLine extends ContextAction {

    public static final String NAME = "TEXT_EDITOR_DUPLICATE_LINE";
    public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN);

    public TextEditorActionDuplicateLine(ResourceProvider provider) {
        super(provider, NAME, RegionTags.TEXT_EDITOR, Messages.ACTION_TEXT_EDITOR_DUPLICATE_LINE, DEFAULT_COMBINATION,
                TextEditorActionRegions.TEXT_MODIFICATION, MainMenuRegion.EDIT, null);
    }

    @Override
    public boolean run(Object node) {
        if (node instanceof CodeFileEditor) {
            ((CodeFileEditor) node).duplicateCurrentLine();
            return true;
        }
        return false;
    }

    @Override
    public void runFromMenu() {
        CodeFileEditorUtils.getFocusedCodeFileEditor().ifPresent(CodeFileEditor::duplicateCurrentLine);
    }

    @Override
    public boolean supportsExplorerState(Explorer explorer) {
        return false;
    }

    @Override
    public boolean supportsTextEditorState(CodeFileEditor editor) {
        return true;
    }

    @Override
    public boolean supportsMainMenuState(MainMenuBar bar) {
        return CodeFileEditorUtils.getFocusedCodeFileEditor().isPresent();
    }
}
