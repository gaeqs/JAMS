package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.architecture.Architecture;

import java.util.Optional;

public class ArchitectureValueConverter extends ValueConverter<Architecture> {

	public static final String NAME = "architecture";

	@Override
	public String toString(Architecture value) {
		return value.getName();
	}

	@Override
	public Optional<Architecture> fromStringSafe(String value) {
		return Jams.getArchitectureManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return Architecture.class;
	}
}
