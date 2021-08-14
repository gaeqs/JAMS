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

package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * Represents a cell inside a {@link CacheMemoryTable}.
 */
public class CacheMemoryTableCell extends TextFieldTableCell<CacheMemoryEntry, String> {

    private final int offset;

    public CacheMemoryTableCell(int offset, Cache cache) {
        super();

        this.offset = offset;

        setConverter(new StringConverter<>() {
            @Override
            public String toString(String object) {
                if (getTableRow() == null) return object;
                CacheMemoryEntry entry = getTableRow().getItem();
                if (entry == null) return object;
                int value = entry.getBlock().getWord(entry.getAddress() + offset, cache.isBigEndian());
                return "0x" + StringUtils.addZeros(Integer.toHexString(value), 8);
            }

            @Override
            public String fromString(String string) {
                CacheMemoryEntry entry = getTableRow().getItem();
                if (entry == null) return null;

                try {
                    int value = NumericUtils.decodeInteger(string);
                    entry.getBlock().setWord(entry.getAddress() + offset, value, cache.isBigEndian());
                    return string;
                } catch (NumberFormatException ex) {
                    if (string.length() > 4) {
                        return getTableRow().getItem().getRepresentation().represent(entry.getBlock(), entry.getAddress() + offset);
                    } else {
                        int i = 0;
                        for (char c : string.toCharArray()) {
                            entry.getBlock().setByte(entry.getAddress() + offset + i++, (byte) c);
                        }
                        return string;
                    }
                }
            }
        });
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        CacheMemoryEntry entry = getTableRow().getItem();
        if (entry == null) return;
        updateItem(entry.getRepresentation().represent(entry.getBlock(), entry.getAddress() + offset), false);
    }

    @Override
    public void commitEdit(String newValue) {
        super.commitEdit(newValue);

        CacheMemoryEntry entry = getTableRow().getItem();
        if (entry == null) return;
        if (entry.getRepresentation().requiresNextWord()) {
            ((CacheMemoryTable) getTableView()).populate();
        }
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null) {
            setText(null);
            setGraphic(null);
            setStyle("-fx-background-color: transparent");
        } else {
            CacheMemoryEntry entry = getTableRow().getItem();
            if (entry == null) {
                setStyle("-fx-background-color: transparent");
                return;
            }

            if (!entry.getRepresentation().isColor()) {
                setText(entry.getRepresentation().represent(entry.getBlock(), entry.getAddress() + offset));
                setStyle("-fx-background-color: transparent");
                return;
            }

            setText("");
            setStyle("-fx-background-color: " + entry.getRepresentation().represent(entry.getBlock(), entry.getAddress() + offset));
        }
    }
}
