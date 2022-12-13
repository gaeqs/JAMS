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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverterManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.function.Consumer;

public class Pow2ValueEditor extends ComboBox<String> implements ValueEditor<Integer> {

    public static final String NAME = "pow2";
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");

    private static final char[] EXPONENTS = new char[]{'\u2070', '\u00B9', '\u00B2', '\u00B3',
            '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079'};

    private Consumer<Integer> listener = i -> {
    };

    public Pow2ValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        StringBuilder builder;
        for (int i = 0; i < 32; i++) {
            builder = new StringBuilder();
            var j = i;
            while (j > 9) {
                builder.append(EXPONENTS[j % 10]);
                j /= 10;
            }
            builder.append(EXPONENTS[j % 10]);

            getItems().add("2" + builder.reverse() + " (" + Integer.toUnsignedString(1 << i) + ")");
        }

        getSelectionModel().select(0);

        getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> listener.accept(val.intValue()));
    }

    @Override
    public Integer getCurrentValue() {
        return getSelectionModel().getSelectedIndex();
    }

    @Override
    public void setCurrentValue(Integer value) {
        getSelectionModel().select(value);
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.getStyleClass().add(GENERAL_STYLE_CLASS + "-hbox");
        box.getStyleClass().add(STYLE_CLASS + "-hbox");
        return box;
    }

    @Override
    public void addListener(Consumer<Integer> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Integer> getLinkedConverter() {
        return Manager.get(ValueConverterManager.class).getByTypeUnsafe(Integer.class);
    }

    public static class Builder implements ValueEditor.Builder<Integer> {

        @Override
        public Class<?> getManagedType() {
            return null;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public ResourceProvider getResourceProvider() {
            return ResourceProvider.JAMS;
        }

        @Override
        public ValueEditor<Integer> build() {
            return new Pow2ValueEditor();
        }

    }
}
