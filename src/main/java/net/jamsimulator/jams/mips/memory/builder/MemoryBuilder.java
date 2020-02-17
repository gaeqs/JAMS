package net.jamsimulator.jams.mips.memory.builder;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents a memory builder. Memory builders are used to create several {@link Memory}
 * using the given parameters.
 * <p>
 * If a plugin want to add a custom memory to JAMS, it should create a child of this class and register
 * it on the {@link net.jamsimulator.jams.manager.AssemblerBuilderManager}.
 */
public abstract class MemoryBuilder {

	private String name;

	/**
	 * Creates a memory builder using a name.
	 * This name must be unique for each memory builder.
	 *
	 * @param name the name.
	 */
	public MemoryBuilder(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	/**
	 * Returns the name of this memory builder.
	 * This name must be unique for each memory builder.
	 *
	 * @return the name of this memory builder.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates a new {@link Memory}.
	 *
	 * @return the new {@link Memory}.
	 */
	public abstract Memory createMemory();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemoryBuilder that = (MemoryBuilder) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
