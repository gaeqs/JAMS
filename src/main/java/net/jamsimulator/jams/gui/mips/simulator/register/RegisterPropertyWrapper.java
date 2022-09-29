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

package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.beans.property.SimpleStringProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.stream.Collectors;

public class RegisterPropertyWrapper {

    public static final String PROPERTY_IDENTIFIER = "identifier";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "value";

    private final RegistersTable table;
    private final Register register;

    private SimpleStringProperty identifierProperty;
    private SimpleStringProperty nameProperty;
    private SimpleStringProperty valueProperty;

    private boolean refreshing = false;

    public RegisterPropertyWrapper(RegistersTable table, Register register) {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(register, "Register cannot be null!");
        this.table = table;
        this.register = register;
    }

    public RegistersTable getTable() {
        return table;
    }

    public Register getRegister() {
        return register;
    }

    public SimpleStringProperty identifierProperty() {
        if (identifierProperty == null) {
            identifierProperty = new SimpleStringProperty(this, PROPERTY_IDENTIFIER);
            refresh();
        }
        return identifierProperty;
    }

    public SimpleStringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(this, PROPERTY_NAME);

            var names = register.getNames();
            var start = register.getRegisters().getValidRegistersStarts().
                    stream().findFirst().map(String::valueOf).orElse("");
            var idAsString = String.valueOf(register.getIdentifier());
            var name = names.stream()
                    .filter(it -> !it.equals(idAsString))
                    .map(it -> start + it)
                    .collect(Collectors.joining(" / "));

            nameProperty.setValue(name);
        }
        return nameProperty;
    }

    public SimpleStringProperty valueProperty() {
        if (valueProperty == null) {
            valueProperty = new SimpleStringProperty(this, PROPERTY_VALUE);
            refresh();

            valueProperty.addListener((obs, old, val) -> {
                if (refreshing || val.equals(old)) return;
                if (!register.isModifiable()) {
                    refresh();
                    return;
                }
                try {
                    int to = NumericUtils.decodeInteger(val);
                    if (register.getValue() != to) {
                        register.setValue(to);
                    }
                } catch (NumberFormatException ex) {
                    try {
                        int to = Float.floatToIntBits(Float.parseFloat(val));
                        if (register.getValue() != to) {
                            register.setValue(to);
                        }
                    } catch (NumberFormatException ignore) {
                    }
                }

                refresh();
            });
        }

        return valueProperty;
    }

    public void refresh() {
        refreshing = true;
        if (identifierProperty != null) {
            if (register.isLocked()) {
                identifierProperty.setValue(register.getIdentifier() + " \uD83D\uDD12");
            } else {
                identifierProperty.setValue(String.valueOf(register.getIdentifier()));
            }
        }

        if (valueProperty != null) {
            valueProperty.setValue(table.getRepresentation().represent(register.getValue(), 0));
        }
        refreshing = false;
    }
}
