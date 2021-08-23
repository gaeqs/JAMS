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

package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.popup.ConfirmationWindow;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;

/**
 * Represents a {@link net.jamsimulator.jams.gui.action.Action}'s {@link KeyCombination}.
 * <p>
 * If clicked, this button opens a window to confirm its removal.
 */
public class ActionExplorerActionCombination extends Button {

    private final ActionsExplorerAction action;
    private final KeyCombination combination;


    public ActionExplorerActionCombination(ActionsExplorerAction action, KeyCombination combination) {
        super(combination.toString());
        getStyleClass().add("action-remove-button");
        this.action = action;
        this.combination = combination;

        setOnAction(event -> ConfirmationWindow.open(Manager.ofS(Language.class).getSelected()
                .getOrDefault(Messages.CONFIG_ACTION_UNBIND), this::deleteCombination, () -> {
        }));
        ellipsisStringProperty().set(combination.toString());
    }

    /**
     * This method is called when the combination must be unbound.
     */
    private void deleteCombination() {
        JamsApplication.getActionManager().unbind(combination, action.getAction().getRegionTag());
        JamsApplication.getActionManager().save();
        action.refresh();
    }

}
