package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Optional;

public class IntegerValueConverter extends ValueConverter<Integer> {

	public static final String NAME = "integer";

	@Override
	public String toString(Integer value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Optional<Integer> fromStringSafe(String value) {
		return NumericUtils.decodeIntegerSafe(value);
	}

	@Override
	public Class<?> conversionClass() {
		return Integer.class;
	}
}
