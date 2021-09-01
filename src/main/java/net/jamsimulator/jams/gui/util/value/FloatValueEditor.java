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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.FloatValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class FloatValueEditor extends TextField implements ValueEditor<Float> {

    public static final String NAME = FloatValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" +NAME;

    protected String oldText;

    private Consumer<Float> listener = f -> {
    };

    public FloatValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        setText("0.0");
        oldText = getText();

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                float number = Float.parseFloat(getText());

                listener.accept(number);

                oldText = getText();
            } catch (NumberFormatException ex) {
                setText(oldText);
            }
        };

        setOnAction(event -> run.run());
        focusedProperty().addListener((obs, old, val) -> {
            if (val) return;
            run.run();
        });
    }

    @Override
    public Float getCurrentValue() {
        return Float.valueOf(getText());
    }

    @Override
    public void setCurrentValue(Float value) {
        setText(String.valueOf(value));
        oldText = getText();
        listener.accept(value);
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
    public void addListener(Consumer<Float> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Float> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Float.class);
    }


    public static class Builder implements ValueEditor.Builder<Float> {

        @Override
        public ValueEditor<Float> build() {
            return new FloatValueEditor();
        }

    }
}
