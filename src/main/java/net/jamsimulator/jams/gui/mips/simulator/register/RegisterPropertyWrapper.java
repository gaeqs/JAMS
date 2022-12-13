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
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.utils.Validate;

import java.util.stream.Collectors;

public class RegisterPropertyWrapper {

    public static final String PROPERTY_IDENTIFIER = "identifier";
    public static final String PROPERTY_SELECTION = "selection";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VALUE = "value";

    private final RegistersTable table;
    private final Register register;
    private final Register nextRegister;

    private SimpleStringProperty identifierProperty;
    private SimpleStringProperty selectionProperty;
    private SimpleStringProperty nameProperty;
    private SimpleStringProperty valueProperty;

    public RegisterPropertyWrapper(RegistersTable table, Register register, Register nextRegister) {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(register, "Register cannot be null!");
        this.table = table;
        this.register = register;
        this.nextRegister = nextRegister;
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

    public SimpleStringProperty selectionProperty() {
        if (selectionProperty == null) {
            selectionProperty = new SimpleStringProperty(this, PROPERTY_SELECTION);
            selectionProperty.setValue(String.valueOf(
                    register instanceof COP0Register cop0 ? cop0.getSelection() : 0));
        }
        return selectionProperty;
    }

    public SimpleStringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(this, PROPERTY_NAME);

            if (register instanceof COP0Register cop0) {
                nameProperty.setValue(cop0.getCop0Name());
            } else {
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
        }
        return nameProperty;
    }

    public SimpleStringProperty valueProperty() {
        if (valueProperty == null) {
            valueProperty = new SimpleStringProperty(this, PROPERTY_VALUE);
            refresh();
        }

        return valueProperty;
    }

    public void refresh() {
        if (identifierProperty != null) {
            if (register.isLocked()) {
                identifierProperty.setValue(register.getIdentifier() + " \uD83D\uDD12");
            } else {
                identifierProperty.setValue(String.valueOf(register.getIdentifier()));
            }
        }

        if (valueProperty != null) {
            valueProperty.setValue(table.getRepresentation()
                    .represent(
                            register.getValue(),
                            nextRegister == null ? 0 : nextRegister.getValue()
                    ));
        }
    }
}
