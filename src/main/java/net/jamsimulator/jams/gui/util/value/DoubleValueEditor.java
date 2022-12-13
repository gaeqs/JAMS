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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.DoubleValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverterManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;

import java.util.function.Consumer;

public class DoubleValueEditor extends TextField implements ValueEditor<Double> {

    public static final String NAME = DoubleValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" +NAME;

    protected String oldText;

    private Consumer<Double> listener = d -> {
    };

    public DoubleValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        setText("0.0");
        oldText = getText();

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                double number = Double.parseDouble(getText());

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
    public Double getCurrentValue() {
        return Double.valueOf(getText());
    }

    @Override
    public void setCurrentValue(Double value) {
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
    public void addListener(Consumer<Double> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Double> getLinkedConverter() {
        return Manager.get(ValueConverterManager.class).getByTypeUnsafe(Double.class);
    }


    public static class Builder implements ValueEditor.Builder<Double> {

        @Override
        public Class<?> getManagedType() {
            return Double.class;
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
        public ValueEditor<Double> build() {
            return new DoubleValueEditor();
        }

    }
}
