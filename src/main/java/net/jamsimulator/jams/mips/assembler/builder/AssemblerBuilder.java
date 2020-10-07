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

package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.Validate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an assembler builder. Assembler builders are used to create several {@link Assembler}
 * using the given parameters.
 * <p>
 * If a plugin want to add a custom assembler to JAMS, it should create a child of this class and register
 * it on the {@link net.jamsimulator.jams.manager.AssemblerBuilderManager}.
 */
public abstract class AssemblerBuilder {

	private final String name;

	/**
	 * Creates an assembler builder using a name.
	 * This name must be unique for each assembler builder.
	 *
	 * @param name the name.
	 */
	public AssemblerBuilder(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	/**
	 * Returns the name of this assembler builder.
	 * This name must be unique for each assembler builder.
	 *
	 * @return the name of this assembler builder.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates an {@link Assembler} using a {@link DirectiveSet}, an {@link InstructionSet}, a {@link Registers}
	 * and a {@link Memory}.
	 *
	 * @param rawFiles       the raw text of all files to assemble.
	 * @param directiveSet   the directive set.
	 * @param instructionSet the instruction set.
	 * @param registerSet    the register set.
	 * @param memory         the memory.
	 * @return the new {@link Assembler}.
	 */
	public abstract Assembler createAssembler(Map<String, String> rawFiles, DirectiveSet directiveSet,
											  InstructionSet instructionSet, Registers registerSet, Memory memory);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssemblerBuilder that = (AssemblerBuilder) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
