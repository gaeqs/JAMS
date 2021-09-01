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
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundleManager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;

import java.util.function.Consumer;

public class SyscallExecutionBuilderBundleValueEditor extends ComboBox<SyscallExecutionBuilderBundle> implements ValueEditor<SyscallExecutionBuilderBundle> {

    public static final String NAME = ActionValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");

    private Consumer<SyscallExecutionBuilderBundle> listener = syscallExecutionBuilderBundle -> {
    };

    public SyscallExecutionBuilderBundleValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        setConverter(ValueConverters.getByTypeUnsafe(SyscallExecutionBuilderBundle.class));
        getItems().addAll(SyscallExecutionBuilderBundleManager.INSTANCE);
        getSelectionModel().select(0);
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Manager.of(SyscallExecutionBuilder.class).registerListeners(this, true);
        SyscallExecutionBuilderBundleManager.INSTANCE.registerListeners(this, true);
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
        box.getStyleClass().add(GENERAL_STYLE_CLASS + "-hbox");
        box.getStyleClass().add(STYLE_CLASS + "-hbox");
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
    private void onSyscallExecutionBuilderBundleRegister(ManagerElementRegisterEvent.After<SyscallExecutionBuilderBundle> event) {
        getItems().add(event.getElement());
    }

    @Listener
    private void onSyscallExecutionBuilderBundleUnregister(ManagerElementUnregisterEvent.After<SyscallExecutionBuilderBundle> event) {
        boolean remove = getSelectionModel().getSelectedItem().equals(event.getElement());
        getItems().remove(event.getElement());
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
