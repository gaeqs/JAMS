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

	private int identifier;
	private Set<String> names;
	private int value;
	private boolean modifiable;
	private int defaultValue;

	/**
	 * Creates a register using a identifier and a list of names.
	 *
	 * @param identifier the identifies.
	 * @param names      the names.
	 */
	public Register(int identifier, String... names) {
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(Arrays.asList(names));
		this.value = defaultValue = 0;
		this.modifiable = true;
	}

	/**
	 * Creates a register using a identifier and a list of names.
	 *
	 * @param identifier the identifies.
	 * @param names      the names.
	 */
	public Register(int identifier, Collection<String> names) {
		Validate.isTrue(names.size() > 0, "A register must have at least one name!");
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
	 * @param identifier the identifier.
	 * @param value      the value.
	 * @param modifiable whether this register is modifiable.
	 * @param names      the names.
	 */
	public Register(int identifier, int value, boolean modifiable, String... names) {
		Validate.isTrue(names.length > 0, "A register must have at least one name!");
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
	 * @param identifier the identifier.
	 * @param value      the value.
	 * @param modifiable whether this register is modifiable.
	 * @param names      the names.
	 */
	public Register(int identifier, int value, boolean modifiable, Collection<String> names) {
		Validate.isTrue(names.size() > 0, "A register must have at least one name!");
		this.identifier = identifier;
		this.names = new HashSet<>();
		this.names.addAll(names);
		this.value = defaultValue = value;
		this.modifiable = modifiable;
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
		this.value = value;
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
	 * Sets this register's value to its initial state.
	 */
	public void reset() {
		value = defaultValue;
	}

	/**
	 * Creates a copy of the register.
	 *
	 * @return the copy.
	 */
	public Register copy() {
		Register register = new Register(identifier, value, modifiable, names);
		register.defaultValue = defaultValue;
		return register;
	}
}
