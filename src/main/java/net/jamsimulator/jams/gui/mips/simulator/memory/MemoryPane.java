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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.ActionRegion;
import net.jamsimulator.jams.gui.action.RegionTags;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.LanguageComboBox;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.NumberRepresentationManager;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.NumberRepresentation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the main pane for the {@link MemoryPane}.
 * <p>
 * Use this to represents a set of memories of a {@link MIPSSimulation}.
 */
public class MemoryPane extends AnchorPane implements ActionRegion {

    private final MIPSSimulation<?> simulation;
    private final ComboBox<String> memorySelector;
    private final ComboBox<NumberRepresentation> representationSelection;
    private final List<NumberRepresentation> representations = new ArrayList<>();
    private final HBox headerHBox, buttonsHBox;
    private MemoryTable table;
    private Memory selected;
    private int savedOffset;

    public MemoryPane(MIPSSimulation<?> simulation) {
        this.simulation = simulation;
        this.savedOffset = 0;

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

        Manager.of(Language.class).registerListeners(this, true);
        Manager.of(NumberRepresentation.class).registerListeners(this, true);
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

        if (table instanceof SimpleMemoryTable) {
            savedOffset = ((SimpleMemoryTable) table).getOffset();
        }

        Region header;
        if (memory instanceof Cache) {
            table = new CacheMemoryTable(simulation, (Cache) memory, 0, representation);
            header = new CacheMemoryHeader((CacheMemoryTable) table);
        } else {
            table = new SimpleMemoryTable(simulation, memory, savedOffset, representation);
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

        if (table instanceof SimpleMemoryTable smt) {
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
                String name = Manager.ofS(Language.class).getSelected()
                        .getOrDefault(((Cache) current).getBuilder().getLanguageNode());

                String data = ((Cache) current).getBlocksAmount() + " / " + ((Cache) current).getBlockSize();

                memorySelector.getItems().add((i++) + " - " + name + " " + data);
            } else {
                String name = Manager.ofS(Language.class).getSelected().getOrDefault(Messages.MEMORY_MEMORY);
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
        Button previous = new Button("\u2190");
        Button next = new Button("\u2192");

        previous.getStyleClass().add("button-bold");
        next.getStyleClass().add("button-bold");
        previous.setPrefWidth(300);
        next.setPrefWidth(300);


        previous.setOnAction(event -> table.previousPage());
        next.setOnAction(event -> table.nextPage());

        buttonsHBox.getChildren().addAll(previous, next);
    }

    private LanguageComboBox<NumberRepresentation> initRepresentationComboBox() {
        representations.addAll(Manager.of(NumberRepresentation.class));
        representations.sort(Comparator.comparing(NumberRepresentation::getName));
        var representationSelection = new LanguageComboBox<>(NumberRepresentation::getLanguageNode);

        representationSelection.setOnAction(event ->
                table.setRepresentation(representationSelection.getSelectionModel().getSelectedItem()));

        representationSelection.getItems().addAll(representations);
        representationSelection.getSelectionModel()
                .select(representations.indexOf(NumberRepresentationManager.HEXADECIMAL));
        return representationSelection;
    }

    @Override
    public boolean supportsActionRegion(String region) {
        return RegionTags.MIPS_SIMULATION.equals(region);
    }

    @Listener
    private void onLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
        loadMemorySelector(simulation.getMemory());
    }

    @Listener
    private void onLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
        loadMemorySelector(simulation.getMemory());
    }

    @Listener
    private void onRepresentationRegister(ManagerElementRegisterEvent.After<NumberRepresentation> event) {
        representations.add(event.getElement());
        refreshRepresentationComboBox();
    }

    @Listener
    private void onRepresentationUnregister(ManagerElementUnregisterEvent.After<NumberRepresentation> event) {
        representations.remove(event.getElement());
        refreshRepresentationComboBox();
    }

    private void refreshRepresentationComboBox() {
        representations.sort(Comparator.comparing(NumberRepresentation::getName));
        representationSelection.getItems().setAll(representations);
        representationSelection.getSelectionModel().select(representations.indexOf(NumberRepresentationManager.HEXADECIMAL));
    }
}
