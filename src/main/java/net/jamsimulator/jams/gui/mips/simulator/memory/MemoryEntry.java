package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class MemoryEntry {

	private final Simulation<?> simulation;

	private final int address;
	private StringProperty pAddress, p0, p4, p8, pC;
	private int d0, d4, d8, dC;

	public MemoryEntry(Simulation<?> simulation, int address) {
		this.simulation = simulation;
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	public StringProperty addressProperty() {
		if (pAddress == null) {
			pAddress = new SimpleStringProperty(null, "address", "0x" + Integer.toHexString(address));
		}

		return pAddress;
	}

	public StringProperty p0Property() {
		if (p0 == null) {
			d0 = simulation.getMemory().getWord(address);
			p0 = new SimpleStringProperty(null, "p0", String.valueOf(d0));
		}

		return p0;
	}

	public StringProperty p4Property() {
		if (p4 == null) {
			d4 = simulation.getMemory().getWord(address);
			p4 = new SimpleStringProperty(null, "p4", String.valueOf(d4));
		}

		return p4;
	}

	public StringProperty p8Property() {
		if (p8 == null) {
			d8 = simulation.getMemory().getWord(address);
			p8 = new SimpleStringProperty(null, "p8", String.valueOf(d8));
		}

		return p8;
	}

	public StringProperty pCProperty() {
		if (pC == null) {
			dC = simulation.getMemory().getWord(address);
			pC = new SimpleStringProperty(null, "pC", String.valueOf(dC));
		}

		return pC;
	}

	public void refresh(Memory memory) {
		update(memory.getWord(address), 0, false);
		update(memory.getWord(address + 4), 4, false);
		update(memory.getWord(address + 8), 8, false);
		update(memory.getWord(address + 12), 12, false);
	}

	public void update(int value, int offset, boolean asFloat) {
		String data = asFloat ? String.valueOf(Float.intBitsToFloat(value)) : String.valueOf(value);
		switch (offset) {
			case 0:
				d0 = value;
				if (p0 == null) break;
				p0.setValue(data);
				break;
			case 4:
				d4 = value;
				if (p4 == null) break;
				p4.setValue(data);
				break;
			case 8:
				d8 = value;
				if (p8 == null) break;
				p8.setValue(data);
				break;
			case 12:
				dC = value;
				if (pC == null) break;
				pC.setValue(data);
				break;
		}
	}

}
