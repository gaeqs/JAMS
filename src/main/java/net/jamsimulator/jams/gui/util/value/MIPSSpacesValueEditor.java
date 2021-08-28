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
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.util.converter.MIPSSpacesValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class MIPSSpacesValueEditor extends ComboBox<MIPSSpaces> implements ValueEditor<MIPSSpaces> {

    public static final String NAME = MIPSSpacesValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");

    private Consumer<MIPSSpaces> listener = mipsSpaces -> {
    };

    public MIPSSpacesValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        setCellFactory(param -> new MIPSSpacesListCell());
        setButtonCell(new MIPSSpacesListCell());
        setConverter(ValueConverters.getByTypeUnsafe(MIPSSpaces.class));
        getItems().addAll(MIPSSpaces.values());
        getSelectionModel().select(0);
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
    }

    @Override
    public MIPSSpaces getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(MIPSSpaces value) {
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
    public void addListener(Consumer<MIPSSpaces> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<MIPSSpaces> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(MIPSSpaces.class);
    }

    public static class Builder implements ValueEditor.Builder<MIPSSpaces> {

        @Override
        public ValueEditor<MIPSSpaces> build() {
            return new MIPSSpacesValueEditor();
        }

    }


    private static class MIPSSpacesListCell extends ListCell<MIPSSpaces> {

        public MIPSSpacesListCell() {
            itemProperty().addListener((obs, old, val) -> setText(val == null ? null : val.getDisplayValue()));
        }

    }
}
