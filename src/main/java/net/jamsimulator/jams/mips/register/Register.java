package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Register {

	private Set<String> names;
	private int value;
	private boolean modifiable;
	private int defaultValue;

	public Register(String... names) {
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
		this.names = new HashSet<>();
		this.names.addAll(Arrays.asList(names));
		this.value = defaultValue = 0;
		this.modifiable = true;
	}


	public Register(int value, boolean modifiable, String... names) {
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
		this.names = new HashSet<>();
		this.names.addAll(Arrays.asList(names));
		this.value = defaultValue = value;
		this.modifiable = modifiable;
	}

	public Set<String> getNames() {
		return new HashSet<>(names);
	}

	public boolean hasName(String name) {
		return names.contains(name);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if (!modifiable) return;
		this.value = value;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public void reset() {
		value = defaultValue;
	}
}
