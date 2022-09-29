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

package net.jamsimulator.jams.gui.mips.simulator.registerold;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Set;

/**
 * This class wraps a {@link Register}, making it valid to be used in a {@link javafx.scene.control.TableView}.
 */
public class RegisterPropertyWrapper {

    private final Object lock = new Object();
    private final Register register;
    private final boolean useDecimals;
    private boolean updating;
    private SimpleStringProperty identifierProperty;
    private SimpleStringProperty nameProperty;
    private SimpleStringProperty valueProperty;
    private SimpleStringProperty hexProperty;

    public RegisterPropertyWrapper(Register register, boolean useDecimals) {
        this.register = register;
        this.useDecimals = useDecimals;
    }

    public Register getRegister() {
        return register;
    }

    public SimpleStringProperty identifierProperty() {
        if (identifierProperty == null) {
            identifierProperty = new SimpleStringProperty(this, "identifier");
            identifierProperty.setValue(String.valueOf(register.getIdentifier()));
        }
        return identifierProperty;
    }

    public SimpleStringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(this, "name");

            Set<String> names = register.getNames();
            StringBuilder builder = new StringBuilder();
            boolean first = true;

            String start = register.getRegisters().getValidRegistersStarts().stream()
                    .findAny().map(String::valueOf).orElse("");
            String idToString = String.valueOf(register.getIdentifier());

            for (String name : names) {
                if (name.equals(idToString)) continue;
                if (first) first = false;
                else builder.append(" / ");
                builder.append(start).append(name);
            }

            nameProperty.setValue(builder.toString());
        }
        return nameProperty;
    }

    public synchronized SimpleStringProperty valueProperty() {
        if (valueProperty == null) {
            valueProperty = new SimpleStringProperty(this, "value");

            if (useDecimals) {
                valueProperty.setValue(String.valueOf(Float.intBitsToFloat(register.getValue())));
            } else {
                valueProperty.setValue(String.valueOf(register.getValue()));
            }

            valueProperty.addListener((obs, old, val) -> {
                synchronized (lock) {
                    if (updating || val.equals(old) || !register.isModifiable()) return;
                    try {
                        int to = useDecimals ? Float.floatToIntBits(Float.parseFloat(val)) : NumericUtils.decodeInteger(val);
                        if (register.getValue() != to) {
                            register.setValue(to);
                        }
                    } catch (NumberFormatException ex) {
                        valueProperty.setValue(old);
                    }
                }
            });
        }
        return valueProperty;
    }

    public StringProperty hexProperty() {
        if (hexProperty == null) {
            hexProperty = new SimpleStringProperty(this, "hex");
            hexProperty.setValue("0x" + StringUtils.addZeros(Integer.toHexString(register.getValue()), 8));
            hexProperty.addListener((obs, old, val) -> {
                synchronized (lock) {
                    if (updating || !register.isModifiable()) return;
                    if (old.equals(val)) return;
                    int to = NumericUtils.decodeInteger(val);
                    if (register.getValue() == to) return;
                    try {
                        register.setValue(to);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        hexProperty.setValue(old);
                    }
                }
            });
        }
        return hexProperty;
    }

    public void updateRegister() {
        updateRegister(register.getValue(), register.isLocked());
    }

    public void updateRegister(int newValue, boolean locked) {
        synchronized (lock) {
            updating = true;
            if (identifierProperty == null) identifierProperty();
            if (valueProperty == null) valueProperty();
            if (hexProperty == null) hexProperty();

            if (locked) {
                identifierProperty.setValue(register.getIdentifier() + " \uD83D\uDD12");
            } else {
                identifierProperty.setValue(String.valueOf(register.getIdentifier()));
            }

            if (useDecimals) {
                valueProperty.setValue(String.valueOf(Float.intBitsToFloat(newValue)));
            } else {
                valueProperty.setValue(String.valueOf(newValue));
            }

            hexProperty.set("0x" + StringUtils.addZeros(Integer.toHexString(newValue), 8));
            updating = false;
        }
    }
}
