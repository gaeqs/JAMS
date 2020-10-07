package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.Theme;

import java.util.Optional;

public class ThemeValueConverter extends ValueConverter<Theme> {

	public static final String NAME = "theme";

	@Override
	public String toString(Theme value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<Theme> fromStringSafe(String value) {
		return JamsApplication.getThemeManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return Theme.class;
	}
}
