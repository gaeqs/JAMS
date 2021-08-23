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
import net.jamsimulator.jams.gui.util.converter.LanguageValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;

import java.util.function.Consumer;

public class LanguageValueEditor extends ComboBox<Language> implements ValueEditor<Language> {

    public static final String NAME = LanguageValueConverter.NAME;

    private Consumer<Language> listener = language -> {
    };

    public LanguageValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(Language.class));
        getItems().addAll(Manager.of(Language.class));
        getSelectionModel().select(Manager.ofS(Language.class).getSelected());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Manager.of(Language.class).registerListeners(this, true);
    }

    @Override
    public Language getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(Language value) {
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
    public void addListener(Consumer<Language> consumer) {
        listener = listener.andThen(consumer);
    }

    @Listener
    private void onLanguageRegister(ManagerElementRegisterEvent.After<Language> event) {
        getItems().add(event.getElement());
    }

    @Listener
    private void onLanguageUnregister(ManagerElementUnregisterEvent.After<Language> event) {
        if (getSelectionModel().getSelectedItem().equals(event.getElement()))
            setValue(Manager.ofD(Language.class).getDefault());
        getItems().remove(event.getElement());
    }

    @Override
    public ValueConverter<Language> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Language.class);
    }

    public static class Builder implements ValueEditor.Builder<Language> {

        @Override
        public ValueEditor<Language> build() {
            return new LanguageValueEditor();
        }

    }
}
