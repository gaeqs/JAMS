package net.jamsimulator.jams.gui.mips.simulator.label;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LabelEntry {

	private final StringProperty name;
	private final StringProperty address;

	public LabelEntry(String name, String address) {
		this.name = new SimpleStringProperty(name);
		this.address = new SimpleStringProperty(address);
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty addressProperty() {
		return address;
	}
}
