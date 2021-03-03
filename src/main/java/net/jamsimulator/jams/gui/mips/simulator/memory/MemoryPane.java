package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.LanguageStringComboBox;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.manager.NumberRepresentationManager;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumberRepresentation;
import net.jamsimulator.jams.utils.representation.event.NumberRepresentationRegisterEvent;
import net.jamsimulator.jams.utils.representation.event.NumberRepresentationUnregisterEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the main pane for the {@link MemoryPane}.
 * <p>
 * Use this to represents a set of memories of a {@link Simulation}.
 */
public class MemoryPane extends AnchorPane implements ActionRegion {

    private final Simulation<?> simulation;

    private MemoryTable table;
    private Memory selected;
    private final ComboBox<String> memorySelector;
    private final ComboBox<String> representationSelection;

    private final List<NumberRepresentation> representations = new ArrayList<>();

    private final HBox headerHBox, buttonsHBox;

    public MemoryPane(Simulation<?> simulation) {
        this.simulation = simulation;

        memorySelector = new ComboBox<>();
        loadMemorySelector(simulation.getMemory());

        buttonsHBox = new HBox();

        representationSelection = initRepresentationComboBox();

        headerHBox = new HBox();
        headerHBox.setSpacing(5);
        headerHBox.getChildren().addAll(memorySelector, representationSelection);
        memorySelector.prefWidthProperty().bind(headerHBox.widthProperty().divide(2));
        representationSelection.prefWidthProperty().bind(headerHBox.widthProperty().divide(2));

        initButtons(buttonsHBox);
        AnchorUtils.setAnchor(headerHBox, 0, -1, 2, 2);
        AnchorUtils.setAnchor(buttonsHBox, -1, 0, 2, 2);


        var memory = simulation.getMemory().getBottomMemory();
        selectMemory(memory);

        Jams.getLanguageManager().registerListeners(this, true);
        Jams.getNumberRepresentationManager().registerListeners(this, true);
    }

    public void selectLastMemory() {
        selectMemory(simulation.getMemory().getBottomMemory());
    }

    public void selectMemory(int index) {
        int i = 0;
        var current = simulation.getMemory();

        while (i < index && current.getNextLevelMemory().isPresent()) {
            current = current.getNextLevelMemory().get();
            i++;
        }

        selectMemory(current);
    }

    private void selectMemory(Memory memory) {
        if (selected == memory) return;
        selected = memory;

        getChildren().clear();

        var representation = table == null ? NumberRepresentationManager.HEXADECIMAL : table.getRepresentation();

        Region header;
        if (memory instanceof Cache) {
            table = new CacheMemoryTable(simulation, (Cache) memory, 0, representation);
            header = new CacheMemoryHeader((CacheMemoryTable) table);
        } else {
            table = new SimpleMemoryTable(simulation, memory, 0, representation);
            header = new SimpleMemoryHeader((SimpleMemoryTable) table);
        }

        header.setPrefHeight(30);

        AnchorUtils.setAnchor(header, 30, -1, 2, 2);
        AnchorUtils.setAnchor((Node) table, 60, 31, 0, 0);

        getChildren().addAll(headerHBox, header, (Node) table, buttonsHBox);
    }

    public void selectAddress(int address) {
        selectLastMemory();
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

    private void loadMemorySelector(Memory memory) {
        memorySelector.getItems().clear();
        int i = 0;
        Memory current = memory;
        while (current != null) {
            if (current instanceof Cache) {
                String name = Jams.getLanguageManager().getSelected()
                        .getOrDefault(((Cache) current).getBuilder().getLanguageNode());

                String data = ((Cache) current).getBlocksAmount() + " / " + ((Cache) current).getBlockSize();

                memorySelector.getItems().add((i++) + " - " + name + " " + data);
            } else {
                String name = Jams.getLanguageManager().getSelected().getOrDefault(Messages.MEMORY_MEMORY);
                memorySelector.getItems().add((i++) + " - " + name);
            }
            current = current.getNextLevelMemory().orElse(null);
        }

        memorySelector.getSelectionModel().selectLast();

        memorySelector.getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> selectMemory(val.intValue()));
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

    private LanguageStringComboBox initRepresentationComboBox() {
        representations.addAll(Jams.getNumberRepresentationManager());
        representations.sort(Comparator.comparing(NumberRepresentation::getName));

        List<String> values = representations.stream().map(NumberRepresentation::getLanguageNode).collect(Collectors.toList());

        var representationSelection = new LanguageStringComboBox(values) {
            @Override
            public void onSelect(int index, String node) {
                table.setRepresentation(representations.get(index));
            }
        };
        representationSelection.getSelectionModel().select(representations.indexOf(NumberRepresentationManager.HEXADECIMAL));
        return representationSelection;
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }

    @Listener
    private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
        loadMemorySelector(simulation.getMemory());
    }

    @Listener
    private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
        loadMemorySelector(simulation.getMemory());
    }

    @Listener
    private void onRepresentationRegister(NumberRepresentationRegisterEvent.After event) {
        representations.add(event.getNumberRepresentation());
        refreshRepresentationComboBox();
    }

    @Listener
    private void onRepresentationUnregister(NumberRepresentationUnregisterEvent.After event) {
        representations.remove(event.getNumberRepresentation());
        refreshRepresentationComboBox();
    }

    private void refreshRepresentationComboBox() {
        representations.sort(Comparator.comparing(NumberRepresentation::getName));
        List<String> values = representations.stream().map(NumberRepresentation::getLanguageNode).collect(Collectors.toList());
        representationSelection.getItems().setAll(values);
        representationSelection.getSelectionModel().select(representations.indexOf(NumberRepresentationManager.HEXADECIMAL));
    }
}
