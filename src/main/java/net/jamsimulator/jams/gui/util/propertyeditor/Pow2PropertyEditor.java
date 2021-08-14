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

package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.function.Consumer;

public class Pow2PropertyEditor extends ComboBox<String> implements PropertyEditor<Integer> {

    private static final char[] EXPONENTS = new char[]{'\u2070', '\u00B9', '\u00B2', '\u00B3',
            '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079'};
    private final Property<Integer> property;
    private Consumer<Integer> listener = p -> {
    };

    public Pow2PropertyEditor(Property<Integer> property, int maxPow) {
        this.property = property;

        StringBuilder builder;
        for (int i = 0; i < maxPow; i++) {
            builder = new StringBuilder();
            var j = i;
            while (j > 9) {
                builder.append(EXPONENTS[j % 10]);
                j /= 10;
            }
            builder.append(EXPONENTS[j % 10]);

            getItems().add("2" + builder.reverse() + " (" + Integer.toUnsignedString(1 << i) + ")");
        }

        getSelectionModel().select(NumericUtils.log2(property.getValue()));
        getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> {
            property.setValue(1 << val.intValue());
            listener.accept(1 << val.intValue());
        });
    }

    @Override
    public Property<? extends Integer> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<Integer> consumer) {
        listener = listener.andThen(consumer);
    }
}
