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
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.*;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderUnregisterEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link SyscallExecutionBuilder}s that projects may use.
 * <p>
 * To register an {@link SyscallExecutionBuilder} use {@link #register(SyscallExecutionBuilder)}.
 * To unregister an {@link SyscallExecutionBuilder} use {@link #unregister(String)}.
 * An {@link SyscallExecutionBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class SyscallExecutionBuilderManager extends SimpleEventBroadcast {

	public static final SyscallExecutionBuilderManager INSTANCE = new SyscallExecutionBuilderManager();

	private final Set<SyscallExecutionBuilder<?>> builders;

	private SyscallExecutionBuilderManager() {
		builders = new HashSet<>();
		addDefaults();
	}

	/**
	 * Returns the {@link SyscallExecutionBuilder} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link SyscallExecutionBuilder}, if present.
	 */
	public Optional<SyscallExecutionBuilder<?>> get(String name) {
		return builders.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link SyscallExecutionBuilder}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<SyscallExecutionBuilder<?>> getAll() {
		return Collections.unmodifiableSet(builders);
	}

	/**
	 * Attempts to register the given {@link SyscallExecutionBuilder} into the manager.
	 * This will fail if a {@link SyscallExecutionBuilder} with the same name already exists within this manager.
	 *
	 * @param builder the builder to register.
	 * @return whether the builder was registered.
	 */
	public boolean register(SyscallExecutionBuilder<?> builder) {
		Validate.notNull(builder, "Builder cannot be null!");

		if (builders.contains(builder)) return false;

		SyscallExecutionBuilderRegisterEvent.Before before =
				callEvent(new SyscallExecutionBuilderRegisterEvent.Before(builder));
		if (before.isCancelled()) return false;

		if (!builders.add(builder)) return false;
		callEvent(new SyscallExecutionBuilderRegisterEvent.After(builder));
		return true;
	}

	/**
	 * Attempts to unregister the {@link SyscallExecutionBuilder} that matches the given name.
	 * This will fail if the {@link SyscallExecutionBuilder} to unregister is the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		SyscallExecutionBuilder<?> builder = builders.stream()
				.filter(target -> target.getName().equals(name))
				.findAny().orElse(null);
		if (builder == null) return false;

		SyscallExecutionBuilderUnregisterEvent.Before before =
				callEvent(new SyscallExecutionBuilderUnregisterEvent.Before(builder));
		if (before.isCancelled()) return false;
		if (!builders.remove(builder)) return false;
		callEvent(new SyscallExecutionBuilderUnregisterEvent.After(builder));
		return true;
	}

	private void addDefaults() {
		builders.add(new SyscallExecutionRunExceptionHandler.Builder());
		builders.add(new SyscallExecutionPrintInteger.Builder());
		builders.add(new SyscallExecutionPrintFloat.Builder());
		builders.add(new SyscallExecutionPrintDouble.Builder());
		builders.add(new SyscallExecutionPrintString.Builder());
		builders.add(new SyscallExecutionReadInteger.Builder());
		builders.add(new SyscallExecutionReadFloat.Builder());
		builders.add(new SyscallExecutionReadDouble.Builder());
		builders.add(new SyscallExecutionReadString.Builder());
		builders.add(new SyscallExecutionAllocateMemory.Builder());
		builders.add(new SyscallExecutionExit.Builder());
		builders.add(new SyscallExecutionPrintCharacter.Builder());
		builders.add(new SyscallExecutionReadCharacter.Builder());
		builders.add(new SyscallExecutionOpenFile.Builder());
		builders.add(new SyscallExecutionReadFile.Builder());
		builders.add(new SyscallExecutionWriteFile.Builder());
		builders.add(new SyscallExecutionCloseFile.Builder());

		builders.add(new SyscallExecutionSleep.Builder());
	}

}
