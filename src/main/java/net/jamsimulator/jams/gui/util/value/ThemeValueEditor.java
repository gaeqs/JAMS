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
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.ThemeRegisterEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeUnregisterEvent;
import net.jamsimulator.jams.gui.util.converter.ThemeValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class ThemeValueEditor extends ComboBox<Theme> implements ValueEditor<Theme> {

    public static final String NAME = ThemeValueConverter.NAME;

    private Consumer<Theme> listener = theme -> {
    };

    public ThemeValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(Theme.class));
        getItems().addAll(JamsApplication.getThemeManager());
        getSelectionModel().select(JamsApplication.getThemeManager().getSelected());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        JamsApplication.getThemeManager().registerListeners(this, true);
    }

    @Override
    public Theme getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(Theme value) {
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
    public void addListener(Consumer<Theme> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Theme> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Theme.class);
    }

    @Listener
    private void onThemeRegister(ThemeRegisterEvent.After event) {
        getItems().add(event.getTheme());
    }

    @Listener
    private void onThemeUnregister(ThemeUnregisterEvent.After event) {
        if (getSelectionModel().getSelectedItem().equals(event.getTheme()))
            setValue(JamsApplication.getThemeManager().stream().findAny().orElse(null));
        getItems().remove(event.getTheme());
    }

    public static class Builder implements ValueEditor.Builder<Theme> {

        @Override
        public ValueEditor<Theme> build() {
            return new ThemeValueEditor();
        }

    }
}
