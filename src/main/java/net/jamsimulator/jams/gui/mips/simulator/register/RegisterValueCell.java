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

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import net.jamsimulator.jams.utils.NumericUtils;

public class RegisterValueCell extends TextFieldTableCell<RegisterPropertyWrapper, String> {

    @Override
    public void startEdit() {
        if (getTableRow().getItem().getRegister().isModifiable()) {
            super.startEdit();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        if (getTableRow().getItem().getTable().getRepresentation().isColor()) {
            setText("");
        }
    }

    @Override
    public void commitEdit(String newValue) {
        var wrapper = getTableRow().getItem();
        var register = wrapper.getRegister();
        var table = wrapper.getTable();

        if (!register.isModifiable()) return;

        try {
            int to = NumericUtils.decodeInteger(newValue);
            if (register.getValue() != to) {
                register.setValue(to);
            }
        } catch (NumberFormatException ex) {
            try {
                int to = Float.floatToIntBits(Float.parseFloat(newValue));
                if (register.getValue() != to) {
                    register.setValue(to);
                }
            } catch (NumberFormatException ignore) {
                return;
            }
        }

        super.commitEdit(newValue);
        table.refreshRepresentation();
    }

    public RegisterValueCell() {
        setConverter(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null) {
            setText(null);
            setGraphic(null);
        } else {
            var entry = getTableRow().getItem();
            if (entry == null) return;
            var representation = entry.getTable().getRepresentation();

            if (!representation.isColor()) {
                setText(item);
                setStyle("-fx-background-color: transparent");
                return;
            }

            setText("");
            setStyle("-fx-background-color: " + item);
        }
    }
}
