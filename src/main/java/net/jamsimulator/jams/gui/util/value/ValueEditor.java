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

package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.Label;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.manager.ManagerResource;

import java.util.function.Consumer;

public interface ValueEditor<E> {

    String GENERAL_STYLE_CLASS = "value-editor";

    default void setCurrentValueUnsafe(Object value) {
        setCurrentValue((E) value);
    }

    E getCurrentValue();

    void setCurrentValue(E value);

    Node getAsNode();

    Node buildConfigNode(Label label);

    void addListener(Consumer<E> consumer);

    ValueConverter<E> getLinkedConverter();

    interface Builder<E> extends ManagerResource {

        Class<?> getManagedType();

        ValueEditor<E> build();

    }

}
