package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.memory.builder.MIPS32MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link MemoryBuilder}s that projects may use.
 * <p>
 * To register an {@link MemoryBuilder} use {@link #register(MemoryBuilder)}.
 * To unregister an {@link MemoryBuilder} use {@link #unregister(String)}.
 * An {@link MemoryBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class MemoryBuilderManager {

	public static final MemoryBuilderManager INSTANCE = new MemoryBuilderManager();

	private MemoryBuilder defaultBuilder;
	private final Set<MemoryBuilder> builders;


	private MemoryBuilderManager() {
		builders = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the default {@link MemoryBuilder} of this manager.
	 * This will be by default a {@link MIPS32MemoryBuilder}.
	 *
	 * @return the default {@link MemoryBuilder}.
	 */
	public MemoryBuilder getDefault() {
		return defaultBuilder;
	}

	/**
	 * Attempts to set the registered {@link MemoryBuilder} that matches the given name
	 * as the default {@link MemoryBuilder}. This will fail if there's no {@link MemoryBuilder}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(String name) {
		Optional<MemoryBuilder> optional = get(name);
		optional.ifPresent(builder -> defaultBuilder = builder);
		return optional.isPresent();
	}

	/**
	 * Returns the {@link MemoryBuilder} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link MemoryBuilder}, if present.
	 */
	public Optional<MemoryBuilder> get(String name) {
		return builders.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link MemoryBuilder}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<MemoryBuilder> getAll() {
		return Collections.unmodifiableSet(builders);
	}

	/**
	 * Attempts to register the given {@link MemoryBuilder} into the manager.
	 * This will fail if a {@link MemoryBuilder} with the same name already exists within this manager.
	 *
	 * @param builder the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(MemoryBuilder builder) {
		Validate.notNull(builder, "Builder cannot be null!");
		return builders.add(builder);
	}

	/**
	 * Attempts to unregisters the {@link MemoryBuilder} that matches the given name.
	 * This will fail if the {@link MemoryBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		if (defaultBuilder.getName().equals(name)) return false;
		return builders.removeIf(target -> target.getName().equals(name));
	}

	private void addDefaults() {
		builders.add(defaultBuilder = new MIPS32MemoryBuilder());
	}

}
