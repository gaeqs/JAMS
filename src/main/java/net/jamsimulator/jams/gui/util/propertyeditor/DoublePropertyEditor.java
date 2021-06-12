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
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class DoublePropertyEditor extends TextField implements PropertyEditor<Double> {

    private final Property<Double> property;
    private Consumer<Double> listener = p -> {
    };
    private String oldText;

    public DoublePropertyEditor(Property<Double> property) {
        this.property = property;

        setText(property.getValue().toString());

        oldText = getText();
        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                double number = Double.parseDouble(getText());
                property.setValue(number);
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
        setPrefWidth(45);
    }

    @Override
    public Property<? extends Double> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<Double> consumer) {
        listener = listener.andThen(consumer);
    }
}
