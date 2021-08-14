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

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.event.CacheResetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.utils.NumberRepresentation;

import java.util.HashMap;

/**
 * Represents the table that shows the content of a cache.
 * This table should be inside a {@link MemoryPane}.
 */
public class CacheMemoryTable extends TableView<CacheMemoryEntry> implements MemoryTable {

    private final HashMap<Integer, CacheMemoryEntry> entries;

    protected MIPSSimulation<?> simulation;
    protected Cache cache;
    protected int block;
    private NumberRepresentation representation;
    private int rows;

    private Runnable onPopulate = () -> {
    };

    public CacheMemoryTable(MIPSSimulation<?> simulation, Cache cache, int block, NumberRepresentation representation) {
        this.simulation = simulation;
        this.cache = cache;
        this.block = block;
        this.representation = representation;

        getStyleClass().add("table-view-horizontal-fit");
        setEditable(true);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        TableColumn<CacheMemoryEntry, String> pAddress = new LanguageTableColumn<>(Messages.MEMORY_ADDRESS);
        TableColumn<CacheMemoryEntry, String> p0 = new TableColumn<>("+0");
        TableColumn<CacheMemoryEntry, String> p4 = new TableColumn<>("+4");
        TableColumn<CacheMemoryEntry, String> p8 = new TableColumn<>("+8");
        TableColumn<CacheMemoryEntry, String> pC = new TableColumn<>("+C");

        p0.setId("0");
        p4.setId("4");
        p8.setId("8");
        pC.setId("C");


        pAddress.setSortable(false);
        p0.setSortable(false);
        p4.setSortable(false);
        p8.setSortable(false);
        pC.setSortable(false);

        pAddress.setCellValueFactory(p -> p.getValue().addressProperty());

        p0.setCellValueFactory(p -> p.getValue().p0Property());
        p4.setCellValueFactory(p -> p.getValue().p4Property());
        p8.setCellValueFactory(p -> p.getValue().p8Property());
        pC.setCellValueFactory(p -> p.getValue().pCProperty());

        p0.setCellFactory(param -> new CacheMemoryTableCell(0, cache));
        p4.setCellFactory(param -> new CacheMemoryTableCell(4, cache));
        p8.setCellFactory(param -> new CacheMemoryTableCell(8, cache));
        pC.setCellFactory(param -> new CacheMemoryTableCell(12, cache));

        getColumns().add(pAddress);
        getColumns().add(p0);
        getColumns().add(p4);
        getColumns().add(p8);
        getColumns().add(pC);

        getVisibleLeafColumn(0).setMinWidth(80);

        entries = new HashMap<>();

        rows = cache.getBlockSize() >> 2;

        if ((cache.getBlocksAmount() & 0x2) != 0) {
            rows++;
        }

        Jams.getMainConfiguration().registerListeners(this, true);

        populate();

        simulation.registerListeners(this, true);
        if (!simulation.isRunning()) {
            this.cache.registerListeners(this, true);
        }
    }

    @Override
    public MIPSSimulation<?> getSimulation() {
        return simulation;
    }

    public int getBlock() {
        return block;
    }

    @Override
    public Cache getMemory() {
        return cache;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public NumberRepresentation getRepresentation() {
        return representation;
    }

    @Override
    public void setRepresentation(NumberRepresentation representation) {
        if (representation == this.representation) return;
        this.representation = representation;
        populate();
    }

    @Override
    public void nextPage() {
        block++;
        if (block >= cache.getBlocksAmount()) block = 0;
        populate();
    }

    @Override
    public void previousPage() {
        block--;
        if (block < 0) block = cache.getBlocksAmount() - 1;
        populate();
    }

    @Override
    public void afterPopulate(Runnable listener) {
        var before = onPopulate;
        onPopulate = () -> {
            before.run();
            listener.run();
        };
    }

    public void populate() {
        getItems().clear();

        var cacheBlock = cache.getCacheBlock(block).orElse(null);
        if (cacheBlock == null) {
            onPopulate.run();
            return;
        }
        int current = 0;

        CacheMemoryEntry entry;
        for (int i = 0; i < rows - 1; i++) {
            entry = new CacheMemoryEntry(cacheBlock, current, 4, representation);
            entries.put(current, entry);
            getItems().add(entry);
            current += 0x10;
        }

        if (rows > 0) {
            int amount = cache.getBlocksAmount() & 0x2;
            if (amount == 0) amount = 4;
            entry = new CacheMemoryEntry(cacheBlock, current, amount, representation);
            entries.put(current, entry);
            getItems().add(entry);
        }

        onPopulate.run();
    }

    //region listeners

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        cache.unregisterListeners(this);
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        cache.registerListeners(this, true);
        populate();
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        populate();
    }

    @Listener
    private void onSimulationCachesReset(SimulationCachesResetEvent event) {
        populate();
    }

    @Listener
    private void onSimulationUndo(SimulationUndoStepEvent.After event) {
        populate();
    }

    @Listener
    private void onMemoryChange(MemoryByteSetEvent.After event) {
        int offset = (event.getAddress() % 16) >> 2 << 2;
        int address = event.getAddress() >> 4 << 4;
        CacheMemoryEntry entry = entries.get(address);
        if (entry == null) return;

        var block = cache.getCacheBlock(this.block).orElse(null);
        if (block == null) return;

        entry.setBlock(block);

        entry.update(event.getAddress(), offset);
    }


    @Listener
    private void onMemoryChange(MemoryWordSetEvent.After event) {
        int offset = event.getAddress() % 16;
        int address = event.getAddress() >> 4 << 4;
        CacheMemoryEntry entry = entries.get(address);
        if (entry == null) return;

        var block = cache.getCacheBlock(this.block).orElse(null);
        if (block == null) return;

        entry.setBlock(block);

        entry.update(event.getAddress(), offset);
    }

    @Listener
    private void onCacheReset(CacheResetEvent.After event) {
        populate();
    }


    //endregion
}
