package net.jamsimulator.jams.mips.syscall;

import javafx.beans.property.Property;

import java.util.List;

public abstract class SyscallExecutionBuilder<Exe extends SyscallExecution> {

	protected final String name;
	protected final List<Property<?>> properties;

	public SyscallExecutionBuilder(String name, List<Property<?>> properties) {
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public List<Property<?>> getProperties() {
		return properties;
	}

	public abstract Exe build();

	public abstract SyscallExecutionBuilder<Exe> makeNewInstance();
}
