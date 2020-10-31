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

import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.bundle.defaults.MARSSyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.bundle.defaults.SPIMSyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.defaults.*;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleUnregisterEvent;
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
 * To register an {@link SyscallExecutionBuilder} use {@link #add(SyscallExecutionBuilder)}.
 * To unregister an {@link SyscallExecutionBuilder} use {@link #remove(Object)}.
 * An {@link SyscallExecutionBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public class SyscallExecutionBuilderManager extends Manager<SyscallExecutionBuilder<?>> {

	public static final SyscallExecutionBuilderManager INSTANCE = new SyscallExecutionBuilderManager();

	private Set<SyscallExecutionBuilderBundle> bundles;

	private SyscallExecutionBuilderManager() {
		super(SyscallExecutionBuilderRegisterEvent.Before::new, SyscallExecutionBuilderRegisterEvent.After::new,
				SyscallExecutionBuilderUnregisterEvent.Before::new, SyscallExecutionBuilderUnregisterEvent.After::new);
	}

	/**
	 * Returns the {@link SyscallExecutionBuilderBundle} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link SyscallExecutionBuilderBundle}, if present.
	 */
	public Optional<SyscallExecutionBuilderBundle> getBundle(String name) {
		return bundles.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns an unmodifiable {@link Set} with all {@link SyscallExecutionBuilderBundle}s registered
	 * in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set}.
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<SyscallExecutionBuilderBundle> getAllBundles() {
		return Collections.unmodifiableSet(bundles);
	}

	/**
	 * Attempts to register the given {@link SyscallExecutionBuilderBundle} into the manager.
	 * This will fail if a {@link SyscallExecutionBuilderBundle} with the same name already exists within this manager.
	 *
	 * @param bundle the bundle to register.
	 * @return whether the bundle was registered.
	 */
	public boolean addBundle(SyscallExecutionBuilderBundle bundle) {
		Validate.notNull(bundle, "Bundle cannot be null!");
		if (bundles.contains(bundle)) return false;

		var before = callEvent(new SyscallExecutionBuilderBundleRegisterEvent.Before(bundle));
		if (before.isCancelled()) return false;
		if (!bundles.add(bundle)) return false;

		callEvent(new SyscallExecutionBuilderBundleRegisterEvent.After(bundle));
		return true;
	}

	/**
	 * Attempts to unregister the given {@link SyscallExecutionBuilderBundle}.
	 *
	 * @param o the bundle.
	 * @return whether the operation was successful.
	 */
	public boolean removeBundle(Object o) {
		if (o == null) return false;
		try {
			var before = callEvent(
					new SyscallExecutionBuilderBundleUnregisterEvent.Before((SyscallExecutionBuilderBundle) o));
			if (before.isCancelled()) return false;
			if (bundles.remove(o)) {
				callEvent(new SyscallExecutionBuilderBundleUnregisterEvent.After((SyscallExecutionBuilderBundle) o));
				return true;
			}
			return false;
		} catch (ClassCastException ex) {
			return false;
		}
	}

	@Override
	protected void loadDefaultElements() {
		add(new SyscallExecutionRunExceptionHandler.Builder());
		add(new SyscallExecutionPrintInteger.Builder());
		add(new SyscallExecutionPrintFloat.Builder());
		add(new SyscallExecutionPrintDouble.Builder());
		add(new SyscallExecutionPrintString.Builder());
		add(new SyscallExecutionReadInteger.Builder());
		add(new SyscallExecutionReadFloat.Builder());
		add(new SyscallExecutionReadDouble.Builder());
		add(new SyscallExecutionReadString.Builder());
		add(new SyscallExecutionAllocateMemory.Builder());
		add(new SyscallExecutionExit.Builder());
		add(new SyscallExecutionPrintCharacter.Builder());
		add(new SyscallExecutionReadCharacter.Builder());
		add(new SyscallExecutionOpenFile.Builder());
		add(new SyscallExecutionReadFile.Builder());
		add(new SyscallExecutionWriteFile.Builder());
		add(new SyscallExecutionCloseFile.Builder());
		add(new SyscallExecutionExitWithValue.Builder());

		add(new SyscallExecutionSystemTime.Builder());
		add(new SyscallExecutionSleep.Builder());

		add(new SyscallExecutionPrintHexadecimalInteger.Builder());
		add(new SyscallExecutionPrintBinaryInteger.Builder());
		add(new SyscallExecutionPrintUnsignedInteger.Builder());
		add(new SyscallExecutionSetSeed.Builder());
		add(new SyscallExecutionRandomInteger.Builder());
		add(new SyscallExecutionRandomRangedInteger.Builder());
		add(new SyscallExecutionRandomFloat.Builder());
		add(new SyscallExecutionRandomDouble.Builder());

		//BUNDLES
		bundles = new HashSet<>();
		addBundle(new SyscallExecutionBuilderBundle("Empty"));
		addBundle(new SPIMSyscallExecutionBuilderBundle());
		addBundle(new MARSSyscallExecutionBuilderBundle());
	}

}
