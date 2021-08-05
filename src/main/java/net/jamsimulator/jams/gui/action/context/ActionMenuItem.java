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

package net.jamsimulator.jams.gui.action.context;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.file.FileType;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;

import java.util.List;

/**
 * A modified {@link MenuItem} that represents an {@link Action}.
 */
public class ActionMenuItem extends MenuItem {

    private final ContextAction action;

    /**
     * Creates the menu item.
     *
     * @param action the represented {@link Action}.
     * @param node   the {@link Node} of the context.
     * @param icon   the shown {@link Image icon} or null.
     */
    public ActionMenuItem(ContextAction action, Object node, Image icon, boolean fromMainMenu) {
        super(Jams.getLanguageManager().getSelected().getOrDefault(action.getLanguageNode().orElse(null)));
        this.action = action;
        setGraphic(new QualityImageView(icon, FileType.IMAGE_SIZE, FileType.IMAGE_SIZE));
        if (fromMainMenu) {
            setOnAction(target -> action.runFromMenu());
        } else {
            setOnAction(target -> action.run(node));
        }
        List<KeyCombination> list = JamsApplication.getActionManager().getBindCombinations(action.getName());
        if (!list.isEmpty()) {
            String text = list.get(0).toString();
            setAccelerator(new KeyCombination() {
                @Override
                public boolean match(KeyEvent event) {
                    return false;
                }

                @Override
                public String toString() {
                    return text;
                }

                @Override
                public String getDisplayText() {
                    return text;
                }
            });
        }
    }

    /**
     * Returns the {@link ContextAction} this item will execute.
     *
     * @return the {@link ContextAction}.
     */
    public ContextAction getAction() {
        return action;
    }
}
