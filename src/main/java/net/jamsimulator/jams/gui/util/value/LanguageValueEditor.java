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
import net.jamsimulator.jams.gui.util.converter.LanguageValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.LanguageRegisterEvent;
import net.jamsimulator.jams.language.event.LanguageUnregisterEvent;

import java.util.function.Consumer;

public class LanguageValueEditor extends ComboBox<Language> implements ValueEditor<Language> {

    public static final String NAME = LanguageValueConverter.NAME;

    private Consumer<Language> listener = language -> {
    };

    public LanguageValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(Language.class));
        getItems().addAll(Jams.getLanguageManager());
        getSelectionModel().select(Jams.getLanguageManager().getSelected());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Jams.getLanguageManager().registerListeners(this, true);
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
    private void onLanguageRegister(LanguageRegisterEvent.After event) {
        getItems().add(event.getLanguage());
    }

    @Listener
    private void onLanguageUnregister(LanguageUnregisterEvent.After event) {
        if (getSelectionModel().getSelectedItem().equals(event.getLanguage()))
            setValue(Jams.getLanguageManager().getDefault());
        getItems().remove(event.getLanguage());
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
