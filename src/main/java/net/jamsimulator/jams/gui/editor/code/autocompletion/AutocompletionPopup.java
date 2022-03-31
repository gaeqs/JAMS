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
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.utils.Validate;

import java.lang.reflect.Method;

/**
 * Represents the tool that allow users to autocomplete their code.
 * <p>
 * An autocompletion popup contains two important components:
 * <ul>
 *     <li>
 *         <strong>The controller:</strong> generates the candidates each time the user opens the autocompletion popup.
 *     </li>
 *     <li>
 *         <strong>The view:</strong> represents the JavaFX node showing all candidates.
 *         It is also responsible of invoking the selection event.
 *     </li>
 * </ul>
 * <p>
 * You must provide both componets to the popup for this tool to work.
 * <p>
 * You can use the view {@link net.jamsimulator.jams.gui.editor.code.autocompletion.view.AutocompletionPopupBasicView
 * used by JAMS by default}.
 */
public class AutocompletionPopup extends Popup implements EventBroadcast {

    private final SimpleEventBroadcast broadcast = new SimpleEventBroadcast();
    private final CodeFileEditor editor;

    private AutocompletionPopupController controller;
    private AutocompletionPopupView view;

    private EditorIndexedElement context;
    private String key;

    /**
     * Creates a new AutocompletionPopup.
     * <p>
     * None of the parameters may be null.
     *
     * @param editor     the editor of this popup.
     * @param controller the controller.
     * @param view       the view.
     */
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

    /**
     * Returns the editor of this popup.
     *
     * @return the {@link CodeFileEditor editor}.
     */
    public CodeFileEditor getEditor() {
        return editor;
    }

    /**
     * Returns the current {@link AutocompletionPopupController controller} of this popup.
     *
     * @return the {@link AutocompletionPopupController controller}.
     */
    public AutocompletionPopupController getController() {
        return controller;
    }

    /**
     * Sets the {@link AutocompletionPopupController controller} of this popup.
     *
     * @param controller the new controller. It can't be null.
     */
    public void setController(AutocompletionPopupController controller) {
        Validate.notNull(controller, "Controller cannot be null!");
        this.controller = controller;
    }

    /**
     * Returs the curret {@link AutocompletionPopupView view} of this popup.
     *
     * @return the {@link AutocompletionPopupView view}.
     */
    public AutocompletionPopupView getView() {
        return view;
    }

    /**
     * Sets the {@link AutocompletionPopupView view} of this popup.
     *
     * @param view the new view. It can't be null.
     */
    public void setView(AutocompletionPopupView view) {
        Validate.notNull(view, "View cannot be null!");
        this.view = view;
        getContent().clear();
        getContent().add(view.asNode());
    }

    /**
     * Populates this autocompletion popup using the current context of this popup's editor.
     * <p>
     * This method must be invoked before {@link #showPopup()} and only if this method returns {@code true}.
     *
     * @param caretOffset       the offset of the caret.
     * @param autocompleteIfOne whether this autocompletion popup should autocomplete automatically
     *                          if the candidates' list contains only one element.
     * @return whether this popup may use the method {@link #showPopup()}.
     */
    public boolean populate(int caretOffset, boolean autocompleteIfOne) {
        int caretPosition = editor.getCaretPosition() + caretOffset;
        if (caretPosition <= 0) return false;

        context = editor.getIndex()
                .withLockF(false, i -> i.getElementAt(caretPosition - 1).orElse(null));
        if (context == null) return false;

        controller.refreshCandidates(context, caretPosition);

        int elementEnd = caretPosition - context.getStart();

        key = context.getText();
        if (elementEnd > 0 && elementEnd < key.length()) {
            key = key.substring(0, elementEnd);
        }

        var list = controller.searchOptions(context, key);
        if (list.isEmpty()) return false;
        if (list.size() == 1 && autocompleteIfOne) {
            autocomplete(list.get(0).candidate().replacement());
            return false;
        }

        view.showContents(this, list);
        return true;
    }

    /**
     * Shows this popup.
     * <p>
     * Make sure to invoke {@link #populate(int, boolean)} before using this method.
     *
     * @see #populate(int, boolean)
     */
    public void showPopup() {
        var bounds = editor.getCaretBounds().orElse(null);
        if (bounds == null) return;
        show(editor, bounds.getMinX(), bounds.getMaxY());
    }

    /**
     * Selects the previous element of this popup.
     * <p>
     * This method invokes a cyclic selection.
     */
    public void moveUp() {
        view.moveUp(this);
    }

    /**
     * Selects the next element of this popup.
     * <p>
     * This method invokes a cyclic selection.
     */
    public void moveDown() {
        view.moveDown(this);
    }

    /**
     * Invokes the autocompletion.
     * <p>
     * This method can only be invoked when this popup is being shown and there's a selected element.
     *
     * @return whether the operation was sucessfull.
     */
    public boolean autocomplete() {
        if (!isShowing()) return false;
        var selected = view.getSelected();
        if (selected.isEmpty()) return false;
        return autocomplete(selected.get());
    }

    protected boolean autocomplete(String replacement) {
        if (!replacement.equals(key)) {
            editor.getUndoManager().preventMerge();
            editor.replaceText(context.getStart(), context.getEnd(), replacement);
        }

        hide();
        return true;
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
