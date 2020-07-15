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

package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a register. A register stores a 32-bit value. If {@link #isModifiable()} is true,
 * then it also can be modified.
 * <p>
 * A register can have several name. Those names shouldn't have a "$" character at the begin, as "$" represents
 * that the string after it is a register; the character isn't part of the name.
 */
public class Register {

	private final Registers registers;

	private final int identifier;
	private final Set<String> names;
	private int value;
	private final boolean modifiable;
	private int defaultValue;

	/**
	 * Creates a register using a identifier and a list of names.
	 *
	 * @param registers  the {@link Registers register set} where this register is stored at.
	 * @param identifier the identifies.
	 * @param names      the names.
	 */
	public Register(Registers registers, int identifier, String... names) {
		Validate.notNull(registers, "Registers cannot be null!");
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
		this.registers = registers;
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(Arrays.asList(names));
		this.value = defaultValue = 0;
		this.modifiable = true;
	}

	/**
	 * Creates a register using a identifier and a list of names.
	 *
	 * @param registers  the {@link Registers register set} where this register is stored at.
	 * @param identifier the identifies.
	 * @param names      the names.
	 */
	public Register(Registers registers, int identifier, Collection<String> names) {
		Validate.notNull(registers, "Registers cannot be null!");
		Validate.isTrue(names.size() > 0, "A register must have at least one name!");
		this.registers = registers;
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(names);
		this.value = defaultValue = 0;
		this.modifiable = true;
	}

	/**
	 * Creates a register using a identifier, a value and a list of names. If the boolean
	 * 'modifiable' is false this register will be read-only.
	 *
	 * @param registers  the {@link Registers register set} where this register is stored at.
	 * @param identifier the identifier.
	 * @param value      the value.
	 * @param modifiable whether this register is modifiable.
	 * @param names      the names.
	 */
	public Register(Registers registers, int identifier, int value, boolean modifiable, String... names) {
		Validate.notNull(registers, "Registers cannot be null!");
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
		this.registers = registers;
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(Arrays.asList(names));
		this.value = defaultValue = value;
		this.modifiable = modifiable;
	}

	/**
	 * Creates a register using a identifier, a value and a list of names. If the boolean
	 * 'modifiable' is false this register will be read-only.
	 *
	 * @param registers  the {@link Registers register set} where this register is stored at.
	 * @param identifier the identifier.
	 * @param value      the value.
	 * @param modifiable whether this register is modifiable.
	 * @param names      the names.
	 */
	public Register(Registers registers, int identifier, int value, boolean modifiable, Collection<String> names) {
		Validate.notNull(registers, "Registers cannot be null!");
		Validate.isTrue(names.size() > 0, "A register must have at least one name!");
		this.registers = registers;
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(names);
		this.value = defaultValue = value;
		this.modifiable = modifiable;
	}

	/**
	 * Returns the {@link Registers register set} this register is inside of.
	 *
	 * @return the {@link Registers register set}.
	 */
	public Registers getRegisters() {
		return registers;
	}

	/**
	 * Returns the identifier of the register.
	 *
	 * @return the identifier.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Returns a immutable {@link Set} with all this register's names.
	 *
	 * @return the {@link Set}.
	 */
	public Set<String> getNames() {
		return new HashSet<>(names);
	}

	/**
	 * Returns whether this register matches the given name.
	 *
	 * @param name the name
	 * @return whether this register matches the given name.
	 */
	public boolean hasName(String name) {
		return names.contains(name);
	}

	/**
	 * Returns the value stored in this register.
	 *
	 * @return the value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the value stored in the register.
	 * If this register is not modifiable this method will do nothing.
	 *
	 * @param value the value.
	 * @see #isModifiable()
	 */
	public void setValue(int value) {
		if (!modifiable) return;
		if (!registers.eventCallsEnabled) {
			this.value = value;
			return;
		}

		RegisterChangeValueEvent.Before before = registers.callEvent(
				new RegisterChangeValueEvent.Before(this, this.value, value));
		if (before.isCancelled()) return;

		int old = this.value;
		this.value = before.getNewValue();

		registers.callEvent(new RegisterChangeValueEvent.After(this, old, this.value));
	}

	/**
	 * Returns whether this register is modifiable.
	 *
	 * @return whether this register is modifiable.
	 */
	public boolean isModifiable() {
		return modifiable;
	}

	/**
	 * Makes the current value the default value.
	 * When the method {@link #reset()} is invoked the current value
	 * will be set to the current value when this method is used.
	 */
	public void makeCurrentValueAsDefault() {
		defaultValue = value;
	}

	/**
	 * Sets this register's value to its initial state.
	 */
	public void reset() {
		setValue(defaultValue);
	}

	/**
	 * Creates a copy of the register.
	 *
	 * @param registers the {@link Registers register set} the copy will be stored at.
	 * @return the copy.
	 */
	public Register copy(Registers registers) {
		Register register = new Register(registers, identifier, value, modifiable, names);
		register.defaultValue = defaultValue;
		return register;
	}
}
