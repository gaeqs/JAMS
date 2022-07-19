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
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.LanguageComboBox;
import net.jamsimulator.jams.gui.util.converter.ALUTypeValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverterManager;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;

import java.util.function.Consumer;

public class ALUTypeValueEditor extends LanguageComboBox<ALUType> implements ValueEditor<ALUType> {

    public static final String NAME = ALUTypeValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");

    private Consumer<ALUType> listener = aluType -> {
    };

    public ALUTypeValueEditor() {
        super(ALUType::languageNode);
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        setConverter(Manager.get(ValueConverterManager.class).getByTypeUnsafe(ALUType.class));
        getItems().addAll(Manager.of(ALUType.class));
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Manager.of(Language.class).registerListeners(this, true);
    }

    @Override
    public ALUType getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(ALUType value) {
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
    public void addListener(Consumer<ALUType> consumer) {
        listener = listener.andThen(consumer);
    }

    @Listener
    private void onALUTypeRegister(ManagerElementRegisterEvent.After<ALUType> event) {
        getItems().add(event.getElement());
    }

    @Listener
    private void onALUTypeUnregister(ManagerElementUnregisterEvent.After<ALUType> event) {
        if (getSelectionModel().getSelectedItem().equals(event.getElement()))
            setValue(Manager.of(ALUType.class).stream().findFirst().orElse(null));
        getItems().remove(event.getElement());
    }

    @Override
    public ValueConverter<ALUType> getLinkedConverter() {
        return Manager.get(ValueConverterManager.class).getByTypeUnsafe(ALUType.class);
    }

    public static class Builder implements ValueEditor.Builder<ALUType> {

        @Override
        public Class<?> getManagedType() {
            return ALUType.class;
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
        public ValueEditor<ALUType> build() {
            return new ALUTypeValueEditor();
        }

    }
}
