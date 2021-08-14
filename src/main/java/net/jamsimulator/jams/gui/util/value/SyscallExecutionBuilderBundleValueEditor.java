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
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleUnregisterEvent;

import java.util.function.Consumer;

public class SyscallExecutionBuilderBundleValueEditor extends ComboBox<SyscallExecutionBuilderBundle> implements ValueEditor<SyscallExecutionBuilderBundle> {

    public static final String NAME = ActionValueConverter.NAME;

    private Consumer<SyscallExecutionBuilderBundle> listener = syscallExecutionBuilderBundle -> {
    };

    public SyscallExecutionBuilderBundleValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(SyscallExecutionBuilderBundle.class));
        getItems().addAll(Jams.getSyscallExecutionBuilderManager().getAllBundles());
        getSelectionModel().select(0);
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Jams.getSyscallExecutionBuilderManager().registerListeners(this, true);
    }

    @Override
    public SyscallExecutionBuilderBundle getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(SyscallExecutionBuilderBundle value) {
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
    public void addListener(Consumer<SyscallExecutionBuilderBundle> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<SyscallExecutionBuilderBundle> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(SyscallExecutionBuilderBundle.class);
    }

    @Listener
    private void onSyscallExecutionBuilderBundleRegister(SyscallExecutionBuilderBundleRegisterEvent.After event) {
        getItems().add(event.getBundle());
    }

    @Listener
    private void onSyscallExecutionBuilderBundleUnregister(SyscallExecutionBuilderBundleUnregisterEvent.After event) {
        boolean remove = getSelectionModel().getSelectedItem().equals(event.getBundle());
        getItems().remove(event.getBundle());
        if (remove) {
            getSelectionModel().select(0);
        }
    }

    public static class Builder implements ValueEditor.Builder<SyscallExecutionBuilderBundle> {

        @Override
        public ValueEditor<SyscallExecutionBuilderBundle> build() {
            return new SyscallExecutionBuilderBundleValueEditor();
        }

    }
}
