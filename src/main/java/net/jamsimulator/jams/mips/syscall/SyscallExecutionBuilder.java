package net.jamsimulator.jams.mips.syscall;

import javafx.beans.property.Property;

import java.util.List;
import java.util.Objects;

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

	public String getLanguageNode() {
		return "SYSCALL_" + name;
	}

	public List<Property<?>> getProperties() {
		return properties;
	}

	public abstract Exe build();

	public abstract SyscallExecutionBuilder<Exe> makeNewInstance();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SyscallExecutionBuilder<?> that = (SyscallExecutionBuilder<?>) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}