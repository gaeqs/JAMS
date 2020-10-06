package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.Optional;

public class SyscallExecutionBuilderValueConverter extends ValueConverter<SyscallExecutionBuilder<?>> {

	public static final String NAME = "syscall_execution_builder";

	@Override
	public String toString(SyscallExecutionBuilder<?> value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<SyscallExecutionBuilder<?>> fromStringSafe(String value) {
		return Jams.getSyscallExecutionBuilderManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return SyscallExecutionBuilder.class;
	}
}
