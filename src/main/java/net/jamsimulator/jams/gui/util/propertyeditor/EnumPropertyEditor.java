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
import javafx.util.StringConverter;
import net.jamsimulator.jams.utils.ReflectionUtils;

import java.util.function.Consumer;

public class EnumPropertyEditor extends ComboBox<Enum<?>> implements PropertyEditor<Enum<?>> {

    private final Property<? super Enum<?>> property;
    private Consumer<Enum<?>> listener = p -> {
    };

    public EnumPropertyEditor(Property<? super Enum<?>> property) {
        this.property = property;
        Enum<?> value = (Enum<?>) property.getValue();
        var clazz = value.getClass();

        try {
            Enum<?>[] values = ReflectionUtils.getEnumValues(clazz);
            for (Enum<?> val : values) {
                getItems().add(val);
            }

            getSelectionModel().select(value);

            setConverter(new StringConverter<>() {
                @Override
                public String toString(Enum<?> object) {
                    return object == null ? null : object.name();
                }

                @Override
                public Enum<?> fromString(String string) {
                    return Enum.valueOf(clazz, string);
                }
            });

            setOnAction(event -> {
                System.out.println(getSelectionModel().getSelectedItem());
                property.setValue(getSelectionModel().getSelectedItem());
                listener.accept(getSelectionModel().getSelectedItem());
            });

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Property<?> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<Enum<?>> consumer) {
        listener = listener.andThen(consumer);
    }
}
