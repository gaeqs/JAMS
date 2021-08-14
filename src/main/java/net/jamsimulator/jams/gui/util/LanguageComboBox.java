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

package net.jamsimulator.jams.gui.util;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.ComboBoxListCell;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.function.Function;

/**
 * A {@link ComboBox} showing its contents in its translated representation.
 * <p>
 * This combo box requires a function that links any element with its language node.
 * <p>
 * This combo box uses a custom cell. Do NOT replace the cell factory.
 *
 * @param <E> the type of elements inside this combo box.
 */
public class LanguageComboBox<E> extends ComboBox<E> {

    private final Function<E, String> languageNodeConversor;

    /**
     * Creates the language combo box.
     *
     * @param languageNodeConversor the function that link any element with its language node.
     */
    public LanguageComboBox(Function<E, String> languageNodeConversor) {
        this.languageNodeConversor = languageNodeConversor;
        setCellFactory(callback -> new Cell());
        setButtonCell(new Cell());
    }

    /**
     * Create sthe language combo box.
     *
     * @param items                 the elements.
     * @param languageNodeConversor the function that link any element with its language node.
     */
    public LanguageComboBox(ObservableList<E> items, Function<E, String> languageNodeConversor) {
        super(items);
        this.languageNodeConversor = languageNodeConversor;
        setCellFactory(callback -> new Cell());
        setButtonCell(new Cell());
    }

    private class Cell extends ComboBoxListCell<E> {

        @Override
        public void updateItem(E item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) setGraphic(null);
            else {
                var languageNode = languageNodeConversor.apply(item);
                setGraphic(new LanguageLabel(languageNode));
                setText(null);
            }
        }
    }
}
