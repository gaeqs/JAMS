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

package net.jamsimulator.jams.mips.register.builder;

import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a registers builder. Registers builders are used to create several {@link Registers}
 * using the given parameters.
 * <p>
 * If a plugin wants to add a custom {@link Registers} to JAMS, it should create a child of this class and register
 * it on the manager.
 */
public abstract class RegistersBuilder {

	protected final String name;

	protected final Set<String> registersNames;
	protected final Set<String> generalRegistersNames;
	protected final Set<String> coprocessor0RegistersNames;
	protected final Set<String> coprocessor1RegistersNames;


	protected final Set<Character> validRegistersStarts;

	/**
	 * Creates the builder.
	 *
	 * @param name                 the name of the builder. This name must be unique.
	 * @param registersNames       the registers that will be created by this builder. These names don't contain any start character.
	 * @param validRegistersStarts the valid starts for registers.
	 */
	public RegistersBuilder(String name, Set<String> registersNames, Set<String> generalRegistersNames,
							Set<String> coprocessor0RegistersNames, Set<String> coprocessor1RegistersNames,
							Set<Character> validRegistersStarts) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(registersNames, "Names cannot be null!");
		Validate.notNull(validRegistersStarts, "Valid registers starts cannot be null!");
		this.name = name;
		this.registersNames = registersNames;
		this.generalRegistersNames = generalRegistersNames;
		this.coprocessor0RegistersNames = coprocessor0RegistersNames;
		this.coprocessor1RegistersNames = coprocessor1RegistersNames;
		this.validRegistersStarts = validRegistersStarts;
	}

	/**
	 * Returns the name of the builder. This name must be unique.
	 *
	 * @return the name of the builder.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a {@link Set} containing all valid starts for a register.
	 * <p>
	 * For example, if this set contains '$', 'r' and 'R' the register s0 can be typed
	 * $s0, rs0 or Rs0.
	 *
	 * @return the {@link Set} containing all valid starts.
	 */
	public Set<Character> getValidRegistersStarts() {
		return Collections.unmodifiableSet(validRegistersStarts);
	}

	/**
	 * Returns a {@link Set} containing all registers' names created by this builder.
	 *
	 * @return the {@link Set}.
	 */
	public Set<String> getRegistersNames() {
		return Collections.unmodifiableSet(registersNames);
	}

	/**
	 * Returns a {@link Set} containing all general registers' names created by this builder.
	 *
	 * @return the {@link Set}.
	 */
	public Set<String> getGeneralRegistersNames() {
		return Collections.unmodifiableSet(generalRegistersNames);
	}

	/**
	 * Returns a {@link Set} containing all coprocessor 0's registers' names created by this builder.
	 *
	 * @return the {@link Set}.
	 */
	public Set<String> getCoprocessor0RegistersNames() {
		return Collections.unmodifiableSet(coprocessor0RegistersNames);
	}

	/**
	 * Returns a {@link Set} containing all coprocessor 1's registers' names created by this builder.
	 *
	 * @return the {@link Set}.
	 */
	public Set<String> getCoprocessor1RegistersNames() {
		return Collections.unmodifiableSet(coprocessor1RegistersNames);
	}

	/**
	 * Returns whether this builder will created a register with the given name.
	 * The name can't contain any start character.
	 *
	 * @param name the name.
	 * @return whether this builder will create the register.
	 */
	public boolean containsRegister(String name) {
		return registersNames.contains(name);
	}

	/**
	 * Creates a {@link Registers} instance.
	 *
	 * @return the instance.
	 */
	public abstract Registers createRegisters();
}
