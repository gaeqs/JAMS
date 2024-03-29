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

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.editor.FileEditor;
import net.jamsimulator.jams.gui.editor.holder.FileEditorTabList;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

public class TextEditorActionPreviousFile extends Action {

    public static final String NAME = "TEXT_EDITOR_PREVIOUS_FILE";
    public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

    public TextEditorActionPreviousFile(ResourceProvider provider) {
        super(provider,NAME, RegionTags.TEXT_EDITOR, Messages.ACTION_TEXT_EDITOR_PREVIOUS_FILE, DEFAULT_COMBINATION);
    }

    @Override
    public boolean run(Object node) {
        if (node instanceof FileEditor) {

            FileEditorTabList list = ((FileEditor) node).getTab().getList();
            list.selectPrevious();
            list.getSelected().ifPresent(target -> {
                if (target.getDisplay() instanceof Node) {
                    ((Node) target.getDisplay()).requestFocus();
                }
            });
            return true;
        }
        return false;
    }
}
