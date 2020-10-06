package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;

import java.util.Optional;

public class SyscallExecutionBuilderBundleValueConverter extends ValueConverter<SyscallExecutionBuilderBundle> {

	public static final String NAME = "SyscallExecutionBuilderBundle";

	@Override
	public String toString(SyscallExecutionBuilderBundle value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<SyscallExecutionBuilderBundle> fromStringSafe(String value) {
		return Jams.getSyscallExecutionBuilderManager().getBundle(value);
	}

	@Override
	public Class<?> conversionClass() {
		return SyscallExecutionBuilder.class;
	}
}
