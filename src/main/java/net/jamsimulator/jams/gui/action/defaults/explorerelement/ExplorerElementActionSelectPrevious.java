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

package net.jamsimulator.jams.gui.action.defaults.explorerelement;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.ResourceProvider;

public class ExplorerElementActionSelectPrevious extends Action {


    public static final String NAME = "EXPLORER_ELEMENT_SELECT_PREVIOUS";
    public static final KeyCombination DEFAULT_COMBINATION = new KeyCodeCombination(KeyCode.UP);

    public ExplorerElementActionSelectPrevious(ResourceProvider provider) {
        super(provider,NAME, RegionTags.EXPLORER_ELEMENT, Messages.ACTION_EXPLORER_ELEMENT_SELECT_PREVIOUS, DEFAULT_COMBINATION);
    }

    @Override
    public boolean run(Object node) {
        if (!(node instanceof ExplorerElement element)) return false;

        Explorer explorer = element.getExplorer();
        explorer.startKeyboardSelection();
        element.getPrevious().ifPresent(target -> {
            explorer.selectElementAlone(target);
            explorer.updateScrollPosition(target);
        });

        return true;
    }
}
