package net.jamsimulator.jams.gui.util.converter;

import javafx.util.StringConverter;
import net.jamsimulator.jams.configuration.Configuration;

import java.util.Optional;

public abstract class ValueConverter<E> extends StringConverter<E> {

	public void save(Configuration configuration, String node, Object value) {
		if (Configuration.isObjectNativelySupported(value)) {
			configuration.set(node, value);
		} else {
			configuration.set(node, toString((E) value));
		}
	}

	public Optional<E> load(Configuration configuration, String node) {
		return configuration.getString(node).flatMap(val ->
				Optional.ofNullable(fromString(val)));
	}

	@Override
	public E fromString(String string) {
		return fromStringSafe(string).orElse(null);
	}

	public abstract Optional<E> fromStringSafe(String node);

	public abstract Class<?> conversionClass();

}
