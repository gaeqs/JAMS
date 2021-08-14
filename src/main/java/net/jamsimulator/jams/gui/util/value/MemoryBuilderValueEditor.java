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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.MemoryBuilderValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderUnregisterEvent;

import java.util.function.Consumer;

public class MemoryBuilderValueEditor extends ComboBox<MemoryBuilder> implements ValueEditor<MemoryBuilder> {

    public static final String NAME = MemoryBuilderValueConverter.NAME;

    private Consumer<MemoryBuilder> listener = memoryBuilder -> {
    };

    public MemoryBuilderValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(MemoryBuilder.class));
        getItems().addAll(Jams.getMemoryBuilderManager());
        getSelectionModel().select(Jams.getMemoryBuilderManager().getDefault());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Jams.getMemoryBuilderManager().registerListeners(this, true);
    }

    @Override
    public MemoryBuilder getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(MemoryBuilder value) {
        getSelectionModel().select(value);
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
    public void addListener(Consumer<MemoryBuilder> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<MemoryBuilder> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(MemoryBuilder.class);
    }

    @Listener
    private void onMemoryBuilderRegister(MemoryBuilderRegisterEvent.After event) {
        getItems().add(event.getMemoryBuilder());
    }

    @Listener
    private void onMemoryBuilderUnregister(MemoryBuilderUnregisterEvent.After event) {
        if (getSelectionModel().getSelectedItem().equals(event.getMemoryBuilder()))
            setValue(Jams.getMemoryBuilderManager().getDefault());
        getItems().remove(event.getMemoryBuilder());
    }

    public static class Builder implements ValueEditor.Builder<MemoryBuilder> {

        @Override
        public ValueEditor<MemoryBuilder> build() {
            return new MemoryBuilderValueEditor();
        }

    }
}
