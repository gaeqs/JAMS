package net.jamsimulator.jams.gui.util.converter;

import java.util.Optional;

public class DoubleValueConverter extends ValueConverter<Double> {

	public static final String NAME = "double";

	@Override
	public String toString(Double value) {
		return value == null ? null : value.toString();
	}

	@Override
	public Optional<Double> fromStringSafe(String value) {
		try {
			return Optional.of(Double.valueOf(value));
		} catch (NumberFormatException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Class<?> conversionClass() {
		return Double.class;
	}
}
