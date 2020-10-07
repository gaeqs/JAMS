package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LabelEntry {

	private final StringProperty name;
	private final StringProperty address;
	private final int addressInt;

	public LabelEntry(String name, String address, int addressInt) {
		this.name = new SimpleStringProperty(name);
		this.address = new SimpleStringProperty(address);
		this.addressInt = addressInt;
	}

	public int getAddressInt() {
		return addressInt;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty addressProperty() {
		return address;
	}
}
