package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.LanguageStringComboBox;
import net.jamsimulator.jams.gui.util.value.HexadecimalIntegerValueEditor;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemoryPane extends AnchorPane implements ActionRegion {

    private final MemoryTable table;

    public MemoryPane(Simulation<?> simulation) {
        var offsetSelection = new ComboBox<String>();
        var directOffsetSelection = new HexadecimalIntegerValueEditor();
        var representationSelection = initRepresentationComboBox();

        var offsetHbox = new HBox();
        var buttonsHBox = new HBox();

        if (simulation.getMemory() instanceof Cache) {
            table = new CacheMemoryTable(simulation, (Cache) simulation.getMemory(), 0, MemoryRepresentation.HEXADECIMAL);
        } else {
            table = new SimpleMemoryTable(simulation, simulation.getMemory(), 0, MemoryRepresentation.HEXADECIMAL);
        }

        initOffsetComboBox(simulation.getMemory(), offsetSelection);
        initDirectOffsetSelection(directOffsetSelection);

        offsetHbox.setSpacing(5);
        offsetHbox.getChildren().addAll(offsetSelection, directOffsetSelection);
        offsetSelection.prefWidthProperty().bind(offsetHbox.widthProperty().divide(2));
        directOffsetSelection.prefWidthProperty().bind(offsetHbox.widthProperty().divide(2));
        initButtons(buttonsHBox);

        AnchorUtils.setAnchor(offsetHbox, 0, -1, 0, 0);
        AnchorUtils.setAnchor(representationSelection, 30, -1, 0, 0);
        AnchorUtils.setAnchor((Node) table, 60, 31, 0, 0);
        AnchorUtils.setAnchor(buttonsHBox, -1, 0, 0, 0);

        getChildren().addAll(offsetHbox, representationSelection, (Node) table, buttonsHBox);
    }

    public void select(int address) {
        var offset = Integer.remainderUnsigned(address, (table.getRows() << 4));
        var start = address - offset;
        var row = offset >>> 4;

        if (table instanceof SimpleMemoryTable) {
            var smt = (SimpleMemoryTable) table;
            smt.setOffset(start);
            smt.getSelectionModel().select(row);
            smt.scrollTo(row);
        }

    }

    private void initOffsetComboBox(Memory memory, ComboBox<String> offsetSelection) {
        if (!(table instanceof SimpleMemoryTable)) return;
        var smt = (SimpleMemoryTable) table;

        List<String> list = new ArrayList<>();
        for (MemorySection section : memory.getMemorySections()) {
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
                smt.setOffset(MIPS32Memory.HEAP);
            }
            if (name.equals("Stack")) {
                smt.setOffset(MIPS32Memory.STACK);
            } else {
                Optional<MemorySection> section = memory.getMemorySection(name);
                section.ifPresent(memorySection -> smt.setOffset(memorySection.getFirstAddress()));
            }
        });
    }

    private void initDirectOffsetSelection(HexadecimalIntegerValueEditor directOffsetSelection) {
        if (!(table instanceof SimpleMemoryTable)) return;
        var smt = (SimpleMemoryTable) table;
        directOffsetSelection.setCurrentValue(smt.getOffset());
        directOffsetSelection.addListener(val -> {
            val = val >> 2 << 2;
            smt.setOffset(val);
        });
    }

    private LanguageStringComboBox initRepresentationComboBox() {
        List<String> values = Arrays.stream(MemoryRepresentation.values())
                .map(MemoryRepresentation::getLanguageNode).collect(Collectors.toList());

        var representationSelection = new LanguageStringComboBox(values) {
            @Override
            public void onSelect(int index, String node) {
                table.setRepresentation(MemoryRepresentation.values()[index]);
            }
        };
        representationSelection.getSelectionModel().select(0);
        return representationSelection;
    }

    private void initButtons(HBox buttonsHBox) {
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.setFillHeight(true);
        Button previous = new Button("←");
        Button next = new Button("→");

        previous.getStyleClass().add("bold-button");
        next.getStyleClass().add("bold-button");
        previous.setPrefWidth(300);
        next.setPrefWidth(300);


        previous.setOnAction(event -> table.previousPage());
        next.setOnAction(event -> table.nextPage());

        buttonsHBox.getChildren().addAll(previous, next);
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }

}
