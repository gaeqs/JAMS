package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.mips.register.builder.MIPS32RegistersBuilder;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link RegistersBuilder}s that projects may use.
 * <p>
 * To register an {@link RegistersBuilder} use {@link #register(RegistersBuilder)}.
 * To unregister an {@link RegistersBuilder} use {@link #unregister(String)}.
 * An {@link RegistersBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class RegistersBuilderManager {

	public static final RegistersBuilderManager INSTANCE = new RegistersBuilderManager();

	private RegistersBuilder defaultBuilder;
	private final Set<RegistersBuilder> builders;


	private RegistersBuilderManager() {
		builders = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the default {@link RegistersBuilder} of this manager.
	 * This will be by default a {@link MIPS32RegistersBuilder}.
	 *
	 * @return the default {@link RegistersBuilder}.
	 */
	public RegistersBuilder getDefault() {
		return defaultBuilder;
	}

	/**
	 * Attempts to set the registered {@link RegistersBuilder} that matches the given name
	 * as the default {@link RegistersBuilder}. This will fail if there's no {@link RegistersBuilder}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(String name) {
		Optional<RegistersBuilder> optional = get(name);
		optional.ifPresent(builder -> defaultBuilder = builder);
		return optional.isPresent();
	}

	/**
	 * Returns the {@link RegistersBuilder} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link RegistersBuilder}, if present.
	 */
	public Optional<RegistersBuilder> get(String name) {
		return builders.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link RegistersBuilder}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<RegistersBuilder> getAll() {
		return Collections.unmodifiableSet(builders);
	}

	/**
	 * Attempts to register the given {@link RegistersBuilder} into the manager.
	 * This will fail if a {@link RegistersBuilder} with the same name already exists within this manager.
	 *
	 * @param builder the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(RegistersBuilder builder) {
		Validate.notNull(builder, "Builder cannot be null!");
		return builders.add(builder);
	}

	/**
	 * Attempts to unregister the {@link RegistersBuilder} that matches the given name.
	 * This will fail if the {@link RegistersBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		if (defaultBuilder.getName().equals(name)) return false;
		return builders.removeIf(target -> target.getName().equals(name));
	}

	private void addDefaults() {
		builders.add(defaultBuilder = new MIPS32RegistersBuilder());
	}

}
