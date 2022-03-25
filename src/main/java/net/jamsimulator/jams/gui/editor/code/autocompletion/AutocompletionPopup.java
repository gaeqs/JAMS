/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.editor.code.autocompletion;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.autocompletion.view.AutocompletionPopupView;
import net.jamsimulator.jams.utils.Validate;

import java.lang.reflect.Method;

public class AutocompletionPopup extends Popup implements EventBroadcast {

    private final SimpleEventBroadcast broadcast = new SimpleEventBroadcast();
    private final CodeFileEditor editor;

    private AutocompletionPopupController controller;
    private AutocompletionPopupView view;

    public AutocompletionPopup(
            CodeFileEditor editor,
            AutocompletionPopupController controller,
            AutocompletionPopupView view
    ) {
        Validate.notNull(editor, "Editor cannot be null!");
        Validate.notNull(controller, "Controller cannot be null!");
        Validate.notNull(view, "View cannot be null!");
        this.editor = editor;
        this.controller = controller;
        this.view = view;

        setAutoFix(true);
        setAutoHide(true);
        getContent().add(view.asNode());

        var oldDispatcher = getEventDispatcher();
        setEventDispatcher((event, tail) -> {
            if (event instanceof MouseEvent && ((MouseEvent) event).getButton() != MouseButton.PRIMARY) {
                return oldDispatcher.dispatchEvent(event, tail);
            }
            return editor.getEventDispatcher().dispatchEvent(event, tail);
        });
    }

    public CodeFileEditor getEditor() {
        return editor;
    }

    public AutocompletionPopupController getController() {
        return controller;
    }

    public void setController(AutocompletionPopupController controller) {
        Validate.notNull(controller, "Controller cannot be null!");
        this.controller = controller;
    }

    public AutocompletionPopupView getView() {
        return view;
    }

    public void setView(AutocompletionPopupView view) {
        Validate.notNull(view, "View cannot be null!");
        this.view = view;
        getContent().clear();
        getContent().add(view.asNode());
    }

    public boolean populate(int caretOffset, boolean autocompleteIfOne) {
        int caretPosition = editor.getCaretPosition() + caretOffset;
        if (caretPosition <= 0) return false;

        var context = editor.getIndex()
                .withLockF(false, i -> i.getElementAt(caretPosition - 1).orElse(null));
        if (context == null) return false;

        controller.refreshCandidates(context);

        int elementEnd = caretPosition - context.getStart();
        var key = context.getText();
        if (elementEnd > 0 && elementEnd < key.length()) {
            key = key.substring(0, elementEnd);
        }

        var list = controller.searchOptions(context, key);
        if (list.isEmpty()) return false;
        if (list.size() == 1 && autocompleteIfOne) {
            // TODO autocomplete
            return false;
        }

        view.showContents(this, list);
        return true;
    }

    public void showPopup() {
        var bounds = editor.getCaretBounds().orElse(null);
        if (bounds == null) return;
        show(editor, bounds.getMinX(), bounds.getMaxY());
    }

    public void moveUp() {
        view.moveUp();
    }

    public void moveDown() {
        view.moveDown();
    }

    //region BROADCAST

    @Override
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        return broadcast.registerListener(instance, method, useWeakReferences);
    }

    @Override
    public int registerListeners(Object instance, boolean useWeakReferences) {
        return broadcast.registerListeners(instance, useWeakReferences);
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        return broadcast.unregisterListener(instance, method);
    }

    @Override
    public int unregisterListeners(Object instance) {
        return broadcast.unregisterListeners(instance);
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return broadcast.callEvent(event, this);
    }

    @Override
    public void transferListenersTo(EventBroadcast broadcast) {
        this.broadcast.transferListenersTo(broadcast);
    }

    //endregion
}
