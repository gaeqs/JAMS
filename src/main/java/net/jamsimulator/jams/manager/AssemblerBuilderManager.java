package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;
import net.jamsimulator.jams.mips.assembler.builder.MIPS32AssemblerBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link AssemblerBuilder}s that projects may use.
 * <p>
 * To register an {@link AssemblerBuilder} use {@link #register(AssemblerBuilder)}.
 * To unregister an {@link AssemblerBuilder} use {@link #unregister(String)}.
 * An {@link AssemblerBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class AssemblerBuilderManager {

	public static final AssemblerBuilderManager INSTANCE = new AssemblerBuilderManager();

	private AssemblerBuilder defaultBuilder;
	private final Set<AssemblerBuilder> builders;


	private AssemblerBuilderManager() {
		builders = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the default {@link AssemblerBuilder} of this manager.
	 * This will be by default a {@link MIPS32AssemblerBuilder}.
	 *
	 * @return the default {@link AssemblerBuilder}.
	 */
	public AssemblerBuilder getDefault() {
		return defaultBuilder;
	}

	/**
	 * Attempts to set the registered {@link AssemblerBuilder} that matches the given name
	 * as the default {@link AssemblerBuilder}. This will fail if there's no {@link AssemblerBuilder}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(String name) {
		Optional<AssemblerBuilder> optional = get(name);
		optional.ifPresent(builder -> defaultBuilder = builder);
		return optional.isPresent();
	}

	/**
	 * Returns the {@link AssemblerBuilder} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link AssemblerBuilder}, if present.
	 */
	public Optional<AssemblerBuilder> get(String name) {
		return builders.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link AssemblerBuilder}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<AssemblerBuilder> getAll() {
		return Collections.unmodifiableSet(builders);
	}

	/**
	 * Attempts to register the given {@link AssemblerBuilder} into the manager.
	 * This will fail if a {@link AssemblerBuilder} with the same name already exists within this manager.
	 *
	 * @param builder the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(AssemblerBuilder builder) {
		Validate.notNull(builder, "Builder cannot be null!");
		return builders.add(builder);
	}

	/**
	 * Attempts to unregister the {@link AssemblerBuilder} that matches the given name.
	 * This will fail if the {@link AssemblerBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		if (defaultBuilder.getName().equals(name)) return false;
		return builders.removeIf(target -> target.getName().equals(name));
	}

	private void addDefaults() {
		builders.add(defaultBuilder = new MIPS32AssemblerBuilder());
	}

}
