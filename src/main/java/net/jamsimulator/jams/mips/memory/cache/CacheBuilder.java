package net.jamsimulator.jams.mips.memory.cache;

import javafx.beans.property.Property;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class CacheBuilder<C extends Cache> {

	protected final String name;
	protected final List<Property<?>> properties;

	public CacheBuilder(String name, List<Property<?>> properties) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
		this.properties = properties == null ? Collections.emptyList() : properties;
	}

	public String getName() {
		return name;
	}

	public String getLanguageNode() {
		return "CACHE_" + name;
	}

	public List<Property<?>> getProperties() {
		return properties;
	}

	public abstract C build(Memory parent);

	public abstract CacheBuilder<C> makeNewInstance();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CacheBuilder<?> that = (CacheBuilder<?>) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
