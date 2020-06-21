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

package net.jamsimulator.jams.mips.instruction.pseudo;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a pseudo-instruction. Pseudo-instructions are MIPS instructions that are separated into several
 * {@link BasicInstruction} in compile time. Pseudo-instructions don't have a direct translation into machine code.
 */
public abstract class PseudoInstruction implements Instruction {

	private String name;
	private String mnemonic;
	private ParameterType[] parameters;


	public PseudoInstruction(String name, String mnemonic, ParameterType[] parameters) {
		this.name = name;
		this.mnemonic = mnemonic;
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMnemonic() {
		return mnemonic;
	}

	@Override
	public ParameterType[] getParameters() {
		return Arrays.copyOf(parameters, parameters.length);
	}

	@Override
	public boolean match(String mnemonic, ParameterType[] parameters) {
		return this.mnemonic.equals(mnemonic) && Arrays.equals(this.parameters, parameters);
	}

	@Override
	public boolean match(String mnemonic, List<ParameterType>[] parameters) {
		if (!this.mnemonic.equalsIgnoreCase(mnemonic)) return false;
		if (parameters.length != this.parameters.length) return false;
		int i = 0;
		for (List<ParameterType> possibilities : parameters) {
			if (!possibilities.contains(this.parameters[i])) return false;
			i++;
		}
		return true;
	}

	/**
	 * Returns the amount of {@link AssembledInstruction}s the
	 * pseudo-instruction will compile at if the given non-compiled parameters are given to it.
	 *
	 * @param parameters the non-compiled parameters.
	 * @return the amount.
	 */
	public int getInstructionAmount(Collection<String> parameters) {
		return getInstructionAmount(parameters.toArray(new String[0]));
	}

	/**
	 * Returns the amount of {@link AssembledInstruction}s the
	 * pseudo-instruction will compile at if the given non-compiled parameters are given to it.
	 *
	 * @param parameters the non-compiled parameters.
	 * @return the amount.
	 */
	public abstract int getInstructionAmount(String[] parameters);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PseudoInstruction that = (PseudoInstruction) o;
		return mnemonic.equals(that.mnemonic) &&
				Arrays.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(mnemonic);
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}
}
