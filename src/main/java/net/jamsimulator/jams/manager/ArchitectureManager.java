/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureRegisterEvent;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureUnregisterEvent;
import net.jamsimulator.jams.mips.architecture.event.DefaultArchitectureChangeEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link Architecture}s that projects may use.
 * <p>
 * To register an {@link Architecture} use {@link #register(Architecture)}.
 * To unregister an {@link Architecture} use {@link #unregister(String)}.
 * An {@link Architecture}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class ArchitectureManager extends SimpleEventBroadcast {

	public static final ArchitectureManager INSTANCE = new ArchitectureManager();

	private Architecture defaultArchitecture;
	private final Set<Architecture> architectures;

	private ArchitectureManager() {
		architectures = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the default {@link Architecture} of this manager.
	 * This will be by default a {@link SingleCycleArchitecture}.
	 *
	 * @return the default {@link Architecture}.
	 */
	public Architecture getDefault() {
		return defaultArchitecture;
	}

	/**
	 * Attempts to set the registered {@link Architecture} that matches the given name
	 * as the default {@link Architecture}. This will fail if there's no {@link Architecture}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(String name) {
		Optional<Architecture> optional = get(name);
		if (!optional.isPresent()) return false;

		DefaultArchitectureChangeEvent.Before before =
				callEvent(new DefaultArchitectureChangeEvent.Before(defaultArchitecture, optional.get()));
		if (before.isCancelled()) return false;

		Architecture old = defaultArchitecture;
		defaultArchitecture = before.getNewArchitecture();

		callEvent(new DefaultArchitectureChangeEvent.After(old, defaultArchitecture));
		return true;
	}

	/**
	 * Returns the {@link Architecture} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link Architecture}, if present.
	 */
	public Optional<Architecture> get(String name) {
		return architectures.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link Architecture}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<Architecture> getAll() {
		return Collections.unmodifiableSet(architectures);
	}

	/**
	 * Attempts to register the given {@link Architecture} into the manager.
	 * This will fail if a {@link Architecture} with the same name already exists within this manager.
	 *
	 * @param architecture the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(Architecture architecture) {
		Validate.notNull(architecture, "Architecture cannot be null!");
		if (architectures.contains(architecture)) return false;

		ArchitectureRegisterEvent.Before before = callEvent(new ArchitectureRegisterEvent.Before(architecture));
		if (before.isCancelled()) return false;

		if (!architectures.add(architecture)) return false;
		callEvent(new ArchitectureRegisterEvent.After(architecture));
		return true;
	}

	/**
	 * Attempts to unregister the {@link Architecture} that matches the given name.
	 * This will fail if the {@link Architecture} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		if (defaultArchitecture.getName().equals(name)) return false;

		Architecture architecture = architectures.stream()
				.filter(target -> target.getName().equals(name))
				.findAny().orElse(null);
		if (architecture == null) return false;

		ArchitectureUnregisterEvent.Before before = callEvent(new ArchitectureUnregisterEvent.Before(architecture));
		if (before.isCancelled()) return false;
		if (!architectures.remove(architecture)) return false;
		callEvent(new ArchitectureUnregisterEvent.After(architecture));
		return true;
	}

	private void addDefaults() {
		architectures.add(defaultArchitecture = SingleCycleArchitecture.INSTANCE);
	}

}
