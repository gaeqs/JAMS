/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ALUValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.mips.instruction.alu.ALU;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;

import java.util.function.Consumer;

public class ALUValueEditor extends HBox implements ValueEditor<ALU> {

    public static final String NAME = ALUValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");

    private final ALUTypeValueEditor aluType = new ALUTypeValueEditor();
    private final RangedIntegerValueEditor cycles = new RangedIntegerValueEditor();

    private Consumer<ALU> listener = alu -> {
    };

    private ALU current = new ALU(ALUType.INTEGER, 1);

    public ALUValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);
        getChildren().addAll(
                new LanguageLabel("SIMULATION_CONFIGURATION_ALUS_TYPE"),
                aluType,
                new LanguageLabel("SIMULATION_CONFIGURATION_ALUS_CYCLES"),
                cycles
        );

        aluType.setCurrentValue(current.type());
        cycles.setMin(1);
        cycles.setCurrentValue(current.cyclesRequired());

        aluType.addListener(it -> refreshValue());
        cycles.addListener(it -> refreshValue());
    }

    @Override
    public ALU getCurrentValue() {
        return current;
    }

    @Override
    public void setCurrentValue(ALU value) {
        aluType.setCurrentValue(value.type());
        cycles.setCurrentValue(value.cyclesRequired());
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.setSpacing(0.5);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<ALU> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<ALU> getLinkedConverter() {
        return null;
    }

    private void refreshValue() {
        current = new ALU(aluType.getCurrentValue(), cycles.getCurrentValue());
        listener.accept(current);
    }

    public static class Builder implements ValueEditor.Builder<ALU> {

        @Override
        public ValueEditor<ALU> build() {
            return new ALUValueEditor();
        }

    }
}
