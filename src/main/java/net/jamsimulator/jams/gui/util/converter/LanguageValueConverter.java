package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.Language;

import java.util.Optional;

public class LanguageValueConverter extends ValueConverter<Language> {

	public static final String NAME = "language";

	@Override
	public String toString(Language value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<Language> fromStringSafe(String value) {
		return Jams.getLanguageManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return Language.class;
	}
}
