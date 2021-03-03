package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.value.HexadecimalIntegerValueEditor;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the header that is showed above the linked {@link SimpleMemoryTable}.
 */
public class SimpleMemoryHeader extends AnchorPane {

    public SimpleMemoryHeader(SimpleMemoryTable table) {
        var offsetSelection = new ComboBox<String>();
        var directOffsetSelection = new HexadecimalIntegerValueEditor();

        var offsetHbox = new HBox();

        initOffsetComboBox(table, offsetSelection);
        initDirectOffsetSelection(table, directOffsetSelection);

        offsetHbox.setSpacing(5);
        offsetHbox.getChildren().addAll(offsetSelection, directOffsetSelection);
        offsetSelection.prefWidthProperty().bind(offsetHbox.widthProperty().divide(2));
        directOffsetSelection.prefWidthProperty().bind(offsetHbox.widthProperty().divide(2));

        AnchorUtils.setAnchor(offsetHbox, 0, -1, 0, 0);

        getChildren().addAll(offsetHbox);
    }


    private void initOffsetComboBox(SimpleMemoryTable table, ComboBox<String> offsetSelection) {

        List<String> list = new ArrayList<>();
        for (MemorySection section : table.getMemory().getMemorySections()) {
            list.add("0x" + StringUtils.addZeros(Integer.toHexString(section.getFirstAddress()), 8) + " - " + section.getName());
        }
        list.add("0x" + StringUtils.addZeros(Integer.toHexString(MIPS32Memory.HEAP), 8) + " - Heap");
        list.add("0x" + StringUtils.addZeros(Integer.toHexString(MIPS32Memory.STACK), 8) + " - Stack");

        list.sort(String::compareTo);
        offsetSelection.getItems().addAll(list);
        offsetSelection.getSelectionModel().select(0);

        offsetSelection.setOnAction(event -> {
            String name = offsetSelection.getSelectionModel().getSelectedItem().substring(13);
            if (name.equals("Heap")) {
                table.setOffset(MIPS32Memory.HEAP);
            }
            if (name.equals("Stack")) {
                table.setOffset(MIPS32Memory.STACK);
            } else {
                Optional<MemorySection> section = table.getMemory().getMemorySection(name);
                section.ifPresent(memorySection -> table.setOffset(memorySection.getFirstAddress()));
            }
        });
    }

    private void initDirectOffsetSelection(SimpleMemoryTable table, HexadecimalIntegerValueEditor directOffsetSelection) {
        directOffsetSelection.setCurrentValue(table.getOffset());
        directOffsetSelection.addListener(val -> {
            val = val >> 2 << 2;
            table.setOffset(val);
        });
    }

}
