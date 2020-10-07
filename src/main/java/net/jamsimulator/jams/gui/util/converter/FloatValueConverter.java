package net.jamsimulator.jams.gui.util.converter;

import java.util.Optional;

public class FloatValueConverter extends ValueConverter<Float> {

	public static final String NAME = "float";

	@Override
	public String toString(Float value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Optional<Float> fromStringSafe(String value) {
		try {
			return Optional.of(Float.valueOf(value));
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Class<?> conversionClass() {
		return Float.class;
	}
}
