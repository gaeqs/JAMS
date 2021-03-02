package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

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
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null) {
            setText(null);
            setGraphic(null);
        } else {
            CacheMemoryEntry entry = getTableRow().getItem();
            if (entry == null) return;

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
