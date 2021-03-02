package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageTableColumn;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationCachesResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationResetEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStartEvent;
import net.jamsimulator.jams.mips.simulation.event.SimulationStopEvent;

import java.util.HashMap;

public class SimpleMemoryTable extends TableView<SimpleMemoryEntry> implements MemoryTable {

    public static final String MEMORY_ROWS_CONFIGURATION_NODE = "simulation.memory_rows";

    private final HashMap<Integer, SimpleMemoryEntry> entries;

    protected Simulation<?> simulation;
    protected Memory memory;
    protected int offset;
    private MemoryRepresentation representation;
    private int rows;

    public SimpleMemoryTable(Simulation<?> simulation, Memory memory, int offset, MemoryRepresentation representation) {
        this.simulation = simulation;
        this.memory = memory;
        this.offset = offset;
        this.representation = representation;
        getStyleClass().add("table-view-horizontal-fit");
        setEditable(true);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        TableColumn<SimpleMemoryEntry, String> pAddress = new LanguageTableColumn<>(Messages.MEMORY_ADDRESS);
        TableColumn<SimpleMemoryEntry, String> p0 = new TableColumn<>("+0");
        TableColumn<SimpleMemoryEntry, String> p4 = new TableColumn<>("+4");
        TableColumn<SimpleMemoryEntry, String> p8 = new TableColumn<>("+8");
        TableColumn<SimpleMemoryEntry, String> pC = new TableColumn<>("+C");

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

        p0.setCellFactory(param -> new SimpleMemoryTableCell(0));
        p4.setCellFactory(param -> new SimpleMemoryTableCell(4));
        p8.setCellFactory(param -> new SimpleMemoryTableCell(8));
        pC.setCellFactory(param -> new SimpleMemoryTableCell(12));

        getColumns().add(pAddress);
        getColumns().add(p0);
        getColumns().add(p4);
        getColumns().add(p8);
        getColumns().add(pC);

        getVisibleLeafColumn(0).setMinWidth(80);

        entries = new HashMap<>();

        rows = (int) Jams.getMainConfiguration().get(MEMORY_ROWS_CONFIGURATION_NODE).orElse(47);
        Jams.getMainConfiguration().registerListeners(this, true);

        populate();

        simulation.registerListeners(this, true);
        if (!simulation.isRunning()) {
            memory.registerListeners(this, true);
        }
    }

    public Simulation<?> getSimulation() {
        return simulation;
    }

    public int getRows() {
        return rows;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (offset == this.offset) return;
        this.offset = offset >> 2 << 2;
        populate();
    }

    public MemoryRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(MemoryRepresentation representation) {
        if (representation == this.representation) return;
        this.representation = representation;
        populate();
    }

    @Override
    public void nextPage() {
        offset += getRows() << 4;
        populate();
    }

    @Override
    public void previousPage() {
        offset -= getRows() << 4;
        populate();
    }

    private void populate() {
        getItems().clear();

        int current = offset;
        SimpleMemoryEntry entry;
        for (int i = 0; i < rows; i++) {
            entry = new SimpleMemoryEntry(memory, current, representation);
            entries.put(current, entry);
            getItems().add(entry);
            current += 0x10;
        }
    }

    @Listener
    private void onSimulationStart(SimulationStartEvent event) {
        memory.unregisterListeners(this);
    }

    @Listener
    private void onSimulationStop(SimulationStopEvent event) {
        memory.registerListeners(this, true);
        entries.values().forEach(SimpleMemoryEntry::refresh);
    }

    @Listener
    private void onSimulationReset(SimulationResetEvent event) {
        entries.values().forEach(SimpleMemoryEntry::refresh);
    }

    @Listener
    private void onSimulationCachesReset(SimulationCachesResetEvent event) {
        entries.values().forEach(SimpleMemoryEntry::refresh);
    }

    @Listener
    private void onMemoryChange(MemoryByteSetEvent.After event) {
        int offset = (event.getAddress() % 16) >> 2 << 2;
        int address = event.getAddress() >> 4 << 4;
        SimpleMemoryEntry entry = entries.get(address);
        if (entry == null) return;
        entry.update(event.getAddress(), offset);
    }


    @Listener
    private void onMemoryChange(MemoryWordSetEvent.After event) {
        int offset = event.getAddress() % 16;
        int address = event.getAddress() >> 4 << 4;
        SimpleMemoryEntry entry = entries.get(address);
        if (entry == null) return;
        entry.update(event.getAddress(), offset);
    }

    @Listener
    private void onConfigurationNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(MEMORY_ROWS_CONFIGURATION_NODE)) {
            rows = (int) event.getNewValueAs().orElse(rows);
            populate();
        }
    }

}
