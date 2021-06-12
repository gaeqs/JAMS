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

package net.jamsimulator.jams.gui.editor.popup;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.editor.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.popup.event.AutocompletionPopupSelectElementEvent;
import net.jamsimulator.jams.gui.util.PixelScrollPane;
import net.jamsimulator.jams.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is a small guide to implement autocompletion popups.
 * <p>
 * It contains the default style and controls.
 */
public abstract class AutocompletionPopup extends Popup implements EventBroadcast {

    protected final CodeFileEditor display;
    protected final VBox content;
    protected final List<AutocompletionPopupElement> elements;
    private final SimpleEventBroadcast broadcast;
    protected AutocompletionPopupElement selected;
    protected int selectedIndex;

    protected ScrollPane scroll;


    /**
     * Creates the autocompletion popup.
     *
     * @param display the code display where this popup is displayed.
     */
    public AutocompletionPopup(CodeFileEditor display) {
        this.broadcast = new SimpleEventBroadcast();
        this.display = display;
        content = new VBox();
        content.getStyleClass().add("autocompletion-popup");
        elements = new ArrayList<>();


        scroll = new PixelScrollPane(content);

        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(200);

        getContent().add(scroll);

        scroll.setOnKeyPressed(event -> {
            managePressEvent(event);
            event.consume();
        });
        scroll.setOnKeyTyped(event -> requestFocus());

        var oldDispatcher = getEventDispatcher();
        setEventDispatcher((event, tail) -> {
            if (event instanceof MouseEvent && ((MouseEvent) event).getButton() != MouseButton.PRIMARY) {
                return oldDispatcher.dispatchEvent(event, tail);
            }
            return display.getEventDispatcher().dispatchEvent(event, tail);
        });
    }

    /**
     * Returns the {@link CodeFileEditor} where this popup is displayed.
     *
     * @return the {@link CodeFileEditor}.
     */
    public CodeFileEditor getDisplay() {
        return display;
    }

    /**
     * Returns whether this popup has no contents.
     * <p>
     * {@link #refreshContents(int)} should be executed before this.
     *
     * @return whether this popup has no contents.
     */
    public boolean isEmpty() {
        return content.getChildren().isEmpty();
    }

    /**
     * Returns the amount of elements inside this popup.
     * <p>
     * {@link #refreshContents(int)} should be executed before this.
     *
     * @return the amount of elements inside this popup.
     */
    public int size() {
        return content.getChildren().size();
    }

    /**
     * Returns the selected element of this autocompletion popup, if present.
     *
     * @return the selected element, if present.
     */
    public Optional<AutocompletionPopupElement> getSelected() {
        return Optional.ofNullable(selected);
    }

    /**
     * Selects the element above the current selected element.
     */
    public void moveUp() {
        selectedIndex--;
        if (selectedIndex < 0) {
            selectedIndex = content.getChildren().size() - 1;
            scroll.setVvalue(1);
            refreshSelected();
        } else {
            refreshSelected();
            updateScrollPosition();
        }

    }

    /**
     * Selects the element below the current selected element.
     */
    public void moveDown() {
        selectedIndex++;
        if (selectedIndex == content.getChildren().size()) {
            selectedIndex = 0;
            scroll.setVvalue(0);
            refreshSelected();
        } else {
            refreshSelected();
            updateScrollPosition();
        }
    }

    /**
     * Selects the element at the given index.
     *
     * @param index the element.
     */
    public void select(int index, boolean updatePosition) {
        while (index < 0) index += elements.size();
        selectedIndex = index % elements.size();
        refreshSelected();
        if (updatePosition) {
            updateScrollPosition();
        }
    }

    /**
     * Adds the given elements to this popup.
     *
     * @param collection               the elements.
     * @param conversion               the function that converts the element into a {@link String}.
     * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
     * @param <T>                      the type of the elements.
     */
    public <T> void addElements(Collection<T> collection, Function<T, String> conversion,
                                Function<T, String> autocompletionConversion, int offset, Image icon) {
        addElements(collection.iterator(), conversion, autocompletionConversion, offset, icon);
    }

    /**
     * Adds the given elements to this popup.
     *
     * @param collection               the elements.
     * @param conversion               the function that converts the element into a {@link String}.
     * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
     * @param <T>                      the type of the elements.
     */
    public <T> void addElements(Stream<T> collection, Function<T, String> conversion,
                                Function<T, String> autocompletionConversion, int offset, Image icon) {
        addElements(collection.iterator(), conversion, autocompletionConversion, offset, icon);
    }

    /**
     * Adds the given elements to this popup.
     *
     * @param iterator                 the elements.
     * @param conversion               the function that converts the element into a {@link String}.
     * @param autocompletionConversion the function that converts the element into the {@link String} used to autocomplete.
     * @param <T>                      the type of the elements.
     */
    public <T> void addElements(Iterator<T> iterator, Function<T, String> conversion,
                                Function<T, String> autocompletionConversion, int offset, Image icon) {
        AutocompletionPopupElement label;
        T next;
        while (iterator.hasNext()) {
            next = iterator.next();
            label = new AutocompletionPopupElement(this, next, elements.size(),
                    StringUtils.addExtraSpaces(conversion.apply(next)),
                    autocompletionConversion.apply(next), offset, icon);
            elements.add(label);
        }
    }

    /**
     * Refreshes the selected element. Used by {@link #moveUp()} and {@link #moveDown()}.
     */
    protected void refreshSelected() {
        if (selected != null) {
            selected.getStyleClass().remove("autocompletion-popup-element-selected");
        }
        selected = (AutocompletionPopupElement) content.getChildren().get(selectedIndex);
        selected.getStyleClass().add("autocompletion-popup-element-selected");
        callEvent(new AutocompletionPopupSelectElementEvent(selected));
    }


    protected void updateScrollPosition() {
        if (selected == null) return;
        Bounds bounds = scroll.getViewportBounds();

        double scrollRelative = selected.getLocalToParentTransform().getTy() + bounds.getMinY();


        //If element is not visible
        double height = getHeight();
        if (scrollRelative < 40) {
            scroll.setVvalue(scroll.getVvalue() + (scrollRelative - 40) / height);
        }
        if (scrollRelative > bounds.getHeight() - 80) {
            scroll.setVvalue(scroll.getVvalue() + (scrollRelative - bounds.getHeight() + 80) / height);
        }
    }


    //region EVENTS

    public void sortAndShowElements(String hint) {
        content.getChildren().clear();
        elements.sort((o1, o2) -> {
            if (o1.getName().equals(hint)) return -1;
            if (o2.getName().equals(hint)) return 1;

            return o1.getName().compareTo(o2.getName());
        });

        var i = 0;
        for (AutocompletionPopupElement element : elements) {
            element.setIndex(i++);
            content.getChildren().add(element);
        }
    }

    /**
     * Manages a {@link CodeFileEditor}'s press event.
     *
     * @param event the event.
     */
    public boolean managePressEvent(KeyEvent event) {
        return manageKeyEvent(event);
    }

    /**
     * Manages a {@link CodeFileEditor}'s press event. This should be called on the filter stage.
     *
     * @param event the event.
     * @return whether the event should be cancelled.
     */
    public boolean manageTypeEvent(KeyEvent event) {
        return manageKeyEvent(event);
    }

    private boolean manageKeyEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.UNDEFINED) return true;
        if (event.isControlDown() || event.isAltDown() || event.isMetaDown() || event.isShortcutDown()) return false;

        return switch (event.getCode()) {
            case RIGHT, LEFT -> {
                if (!isShowing()) yield false;
                var right = event.getCode() == KeyCode.RIGHT;
                execute(right ? 1 : -1, false);
                display.moveTo(display.getCaretPosition() + (right ? 1 : -1));
                yield true;
            }
            case BACK_SPACE -> {
                if (!isShowing()) yield false;

                execute(-1, false);
                IndexRange selection = display.getSelection();
                if (selection.getLength() == 0) {
                    display.deletePreviousChar();
                } else {
                    display.replaceSelection("");
                }

                yield true;
            }
            case UP -> {
                if (!isShowing()) yield false;
                moveUp();
                yield true;
            }
            case DOWN -> {
                if (!isShowing()) yield false;
                moveDown();
                yield true;
            }
            case ENTER, TAB -> {
                if (!isShowing()) yield false;
                autocomplete();
                hide();
                yield true;
            }
            case ESCAPE, SPACE -> {
                hide();
                yield true;
            }
            default -> {
                Platform.runLater(() -> execute(0, false));
                yield false;
            }
        };
    }

    //endregion

    /**
     * Tries to open the popup at the caret position, refreshing it.
     * The caret position can be modified using the parameter 'caretOffset'.
     * <p>
     * If 'autocompleteIfOne' is true and there's only one element inside the popup the popup won't open
     * and it will call the method {@link #autocomplete()}.
     *
     * @param caretOffset       the caret offset.
     * @param autocompleteIfOne whether it should autocomplete instead of opening of there's only one element.
     */
    public abstract void execute(int caretOffset, boolean autocompleteIfOne);

    /**
     * Refreshes the contents inside this popup.
     *
     * @param caretPosition the caret position.
     */
    public abstract void refreshContents(int caretPosition);

    /**
     * Autocompletes using the selected element.
     */
    public abstract void autocomplete();


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

    //endregion

}
