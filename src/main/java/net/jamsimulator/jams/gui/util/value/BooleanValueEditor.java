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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.BooleanValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class BooleanValueEditor extends CheckBox implements ValueEditor<Boolean> {

    public static final String NAME = BooleanValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" +NAME;

    private Consumer<Boolean> listener = b -> {
    };

    public BooleanValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        selectedProperty().addListener((obs, old, val) -> listener.accept(val));
    }

    @Override
    public Boolean getCurrentValue() {
        return isSelected();
    }

    @Override
    public void setCurrentValue(Boolean value) {
        setSelected(value);
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        label.setOnMouseClicked(click -> setCurrentValueUnsafe(!((boolean) getCurrentValue())));
        var box = new HBox(this, label);
        box.getStyleClass().add(GENERAL_STYLE_CLASS + "-hbox");
        box.getStyleClass().add(STYLE_CLASS + "-hbox");
        return box;
    }

    @Override
    public void addListener(Consumer<Boolean> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Boolean> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Boolean.class);
    }


    public static class Builder implements ValueEditor.Builder<Boolean> {

        @Override
        public ValueEditor<Boolean> build() {
            return new BooleanValueEditor();
        }

    }
}
