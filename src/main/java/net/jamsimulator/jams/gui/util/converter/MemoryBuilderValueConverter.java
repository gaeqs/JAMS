package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;

import java.util.Optional;

public class MemoryBuilderValueConverter extends ValueConverter<MemoryBuilder> {

	public static final String NAME = "memory_builder";

	@Override
	public String toString(MemoryBuilder value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<MemoryBuilder> fromStringSafe(String value) {
		return Jams.getMemoryBuilderManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return MemoryBuilder.class;
	}
}
