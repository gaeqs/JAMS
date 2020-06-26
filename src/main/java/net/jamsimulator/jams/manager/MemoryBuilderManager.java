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
import net.jamsimulator.jams.mips.memory.builder.MIPS32MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;
import net.jamsimulator.jams.mips.memory.builder.event.DefaultMemoryBuilderChangeEvent;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.builder.event.MemoryBuilderUnregisterEvent;
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
public class MemoryBuilderManager extends SimpleEventBroadcast {

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
		if (!optional.isPresent()) return false;

		DefaultMemoryBuilderChangeEvent.Before before =
				callEvent(new DefaultMemoryBuilderChangeEvent.Before(defaultBuilder, optional.get()));
		if (before.isCancelled()) return false;

		MemoryBuilder old = defaultBuilder;
		defaultBuilder = before.getNewMemoryBuilder();

		callEvent(new DefaultMemoryBuilderChangeEvent.After(old, defaultBuilder));
		return true;
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

		if (builders.contains(builder)) return false;

		MemoryBuilderRegisterEvent.Before before = callEvent(new MemoryBuilderRegisterEvent.Before(builder));
		if (before.isCancelled()) return false;

		if (!builders.add(builder)) return false;
		callEvent(new MemoryBuilderRegisterEvent.After(builder));
		return true;
	}

	/**
	 * Attempts to unregister the {@link MemoryBuilder} that matches the given name.
	 * This will fail if the {@link MemoryBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		if (defaultBuilder.getName().equals(name)) return false;

		MemoryBuilder builder = builders.stream()
				.filter(target -> target.getName().equals(name))
				.findAny().orElse(null);
		if (builder == null) return false;

		MemoryBuilderUnregisterEvent.Before before =
				callEvent(new MemoryBuilderUnregisterEvent.Before(builder));
		if (before.isCancelled()) return false;
		if (!builders.remove(builder)) return false;
		callEvent(new MemoryBuilderUnregisterEvent.After(builder));
		return true;
	}

	private void addDefaults() {
		builders.add(defaultBuilder = new MIPS32MemoryBuilder());
	}

}
