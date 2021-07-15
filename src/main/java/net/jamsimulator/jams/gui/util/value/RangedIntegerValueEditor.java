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
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.function.Consumer;

public class RangedIntegerValueEditor extends TextField implements ValueEditor<Integer> {

    public static final String NAME = "positive_integer";

    protected String oldText;
    protected int min, max;

    private Consumer<Integer> listener = i -> {
    };

    public RangedIntegerValueEditor() {
        setText("0");
        oldText = getText();
        min = Integer.MIN_VALUE;
        max = Integer.MAX_VALUE;

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                int number = NumericUtils.decodeInteger(getText());
                if (number < min || number > max) {
                    setText(oldText);
                    return;
                }

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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        setCurrentValue(getCurrentValue());
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        setCurrentValue(getCurrentValue());
    }

    @Override
    public Integer getCurrentValue() {
        return NumericUtils.decodeInteger(getText());
    }

    @Override
    public void setCurrentValue(Integer value) {
        int positive = Math.max(min, Math.min(max, value));
        setText(oldText = String.valueOf(positive));
        listener.accept(positive);
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<Integer> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Integer> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Integer.class);
    }

    public static class Builder implements ValueEditor.Builder<Integer> {

        @Override
        public ValueEditor<Integer> build() {
            return new RangedIntegerValueEditor();
        }

    }
}
