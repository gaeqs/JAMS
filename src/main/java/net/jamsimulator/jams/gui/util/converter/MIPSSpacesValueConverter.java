package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;

import java.util.Optional;

public class MIPSSpacesValueConverter extends ValueConverter<MIPSSpaces> {

	public static final String NAME = "mips_spaces";

	@Override
	public String toString(MIPSSpaces value) {
		return value == null ? null : value.name();
	}

	@Override
	public Optional<MIPSSpaces> fromStringSafe(String value) {
		return MIPSSpaces.getByValue(value);
	}

	@Override
	public Class<?> conversionClass() {
		return MIPSSpaces.class;
	}
}
