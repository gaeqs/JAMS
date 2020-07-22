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
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.AssociativeCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.DirectCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.builder.SetAssociativeCacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderUnregisterEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link CacheBuilder}s that projects may use.
 * <p>
 * To register an {@link CacheBuilder} use {@link #register(CacheBuilder)}.
 * To unregister an {@link CacheBuilder} use {@link #unregister(String)}.
 * An {@link CacheBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class CacheBuilderManager extends SimpleEventBroadcast {

	public static final CacheBuilderManager INSTANCE = new CacheBuilderManager();

	private final Set<CacheBuilder<?>> builders;


	private CacheBuilderManager() {
		builders = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the {@link CacheBuilder} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link CacheBuilder}, if present.
	 */
	public Optional<CacheBuilder<?>> get(String name) {
		return builders.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link CacheBuilder}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<CacheBuilder<?>> getAll() {
		return Collections.unmodifiableSet(builders);
	}

	/**
	 * Attempts to register the given {@link CacheBuilder} into the manager.
	 * This will fail if a {@link CacheBuilder} with the same name already exists within this manager.
	 *
	 * @param builder the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(CacheBuilder<?> builder) {
		Validate.notNull(builder, "Builder cannot be null!");

		if (builders.contains(builder)) return false;

		CacheBuilderRegisterEvent.Before before = callEvent(new CacheBuilderRegisterEvent.Before(builder));
		if (before.isCancelled()) return false;

		if (!builders.add(builder)) return false;
		callEvent(new CacheBuilderRegisterEvent.After(builder));
		return true;
	}

	/**
	 * Attempts to unregister the {@link CacheBuilder} that matches the given name.
	 * This will fail if the {@link CacheBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		CacheBuilder<?> builder = builders.stream()
				.filter(target -> target.getName().equals(name))
				.findAny().orElse(null);
		if (builder == null) return false;

		CacheBuilderUnregisterEvent.Before before =
				callEvent(new CacheBuilderUnregisterEvent.Before(builder));
		if (before.isCancelled()) return false;
		if (!builders.remove(builder)) return false;
		callEvent(new CacheBuilderUnregisterEvent.After(builder));
		return true;
	}

	private void addDefaults() {
		builders.add(new AssociativeCacheBuilder());
		builders.add(new DirectCacheBuilder());
		builders.add(new SetAssociativeCacheBuilder());
	}

}
