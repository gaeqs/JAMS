package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;

import java.util.Optional;

public class CacheBuilderValueConverter extends ValueConverter<CacheBuilder<?>> {

	public static final String NAME = "cache_builder";

	@Override
	public String toString(CacheBuilder<?> value) {
		return value == null ? null : value.getName();
	}

	@Override
	public Optional<CacheBuilder<?>> fromStringSafe(String value) {
		return Jams.getCacheBuilderManager().get(value);
	}

	@Override
	public Class<?> conversionClass() {
		return CacheBuilder.class;
	}
}
