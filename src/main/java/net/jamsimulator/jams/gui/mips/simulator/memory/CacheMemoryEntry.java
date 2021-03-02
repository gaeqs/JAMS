package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.utils.StringUtils;

public class CacheMemoryEntry {

    private CacheBlock block;
    private final int address;
    private final int representAmount;
    private final MemoryRepresentation representation;

    private StringProperty pAddress, p0, p4, p8, pC;

    public CacheMemoryEntry(CacheBlock block, int address, int representAmount, MemoryRepresentation representation) {
        this.block = block;
        this.address = address;
        this.representAmount = representAmount;
        this.representation = representation;
    }


    public CacheBlock getBlock() {
        return block;
    }

    public int getAddress() {
        return address;
    }

    public MemoryRepresentation getRepresentation() {
        return representation;
    }

    public StringProperty addressProperty() {
        if (pAddress == null) {
            pAddress = new SimpleStringProperty(null, "address", "0x" + StringUtils.addZeros(Integer.toHexString(address), 8));
        }

        return pAddress;
    }

    public StringProperty p0Property() {
        if (p0 == null) {
            p0 = new SimpleStringProperty(null, "p0", represent(address));
        }

        return p0;
    }

    public StringProperty p4Property() {
        if (p4 == null) {
            if (representAmount < 2)
                p4 = new SimpleStringProperty(null, "p4", "-");
            else
                p4 = new SimpleStringProperty(null, "p4", represent(address + 4));
        }

        return p4;
    }

    public StringProperty p8Property() {
        if (p8 == null) {
            if (representAmount < 3)
                p8 = new SimpleStringProperty(null, "p8", "-");
            else
                p8 = new SimpleStringProperty(null, "p8", represent(address + 8));
        }

        return p8;
    }

    public StringProperty pCProperty() {
        if (representAmount < 4)
            pC = new SimpleStringProperty(null, "pC", "-");
        else if (pC == null)
            pC = new SimpleStringProperty(null, "pC", represent(address + 12));

        return pC;
    }

    public void refresh(CacheBlock block) {
        this.block = block;
        update(address, 0);
        update(address + 4, 4);
        update(address + 8, 8);
        update(address + 12, 12);
    }

    public void setBlock(CacheBlock block) {
        this.block = block;
    }

    public void update(int address, int offset) {
        String data = represent(address);
        switch (offset) {
            case 0 -> {
                if (p0 == null) break;
                p0.setValue(data);
            }
            case 4 -> {
                if (p4 == null) break;
                p4.setValue(data);
            }
            case 8 -> {
                if (p8 == null) break;
                p8.setValue(data);
            }
            case 12 -> {
                if (pC == null) break;
                pC.setValue(data);
            }
        }
    }

    private String represent(int address) {
        return representation.represent(block, address);
    }
}
