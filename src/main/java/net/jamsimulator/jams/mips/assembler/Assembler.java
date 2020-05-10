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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.List;
import java.util.Map;

/**
 * Represents a assembler. A compile transforms a list of files into a {@link Simulation}.
 * To create a simulation you must use the method {@link #compile()} before.
 * <p>
 * Each created {@link Simulation} will have a copy of this assembler's memory and registers.
 */
public interface Assembler {

	/**
	 * Sets the data to compile.
	 *
	 * @param data the data.
	 */
	void setData(List<List<String>> data);

	/**
	 * Compiles the program.
	 *
	 * @throws AssemblerException    if any compilation exception occurs.
	 * @throws IllegalStateException if the assembler has already compiled its code
	 *                               or if the assembler has no data.
	 */
	void compile();

	/**
	 * Creates a new {@link Simulation} using the compiled data of this assembler.
	 * <p>
	 * The {@link Simulation}'s {@link Memory} and {@link Registers} is a copy of this assembler's data.
	 * Two {@link Simulation}s created by this method will have different memories and registers.
	 *
	 * @param architecture the {@link Architecture}.
	 * @param <Arch>       the architecture type.
	 * @return the new {@link Simulation}.
	 * @throws IllegalStateException whether the assembler is not compiled.
	 * @see #compile()
	 */
	<Arch extends Architecture> Simulation<Arch> createSimulation(Arch architecture);

	/**
	 * Returns whether this assembler has compiled its code.
	 *
	 * @return whether this assembler has compiled its code.
	 * @see #compile()
	 */
	boolean isCompiled();


	/**
	 * Returns this simulation's {@link InstructionSet}.
	 *
	 * @return the {@link InstructionSet}.
	 */
	InstructionSet getInstructionSet();

	/**
	 * Returns this simulation's {@link Registers}.
	 * You can modify this {@link Registers}.
	 *
	 * @return the {@link Registers}.
	 */
	Registers getRegisterSet();

	/**
	 * Returns this simulation's {@link Memory}.
	 * You can modify this {@link Memory}.
	 *
	 * @return the {@link Memory}.
	 */
	Memory getMemory();

	/**
	 * Gets this assembler's data.
	 *
	 * @return the {@link AssemblerData}.
	 */
	AssemblerData getAssemblerData();

	/**
	 * Gets the file that this assembler is currently compiling.
	 * If the assembler has finished, this method will return the last {@link AssemblingFile}.
	 *
	 * @return the {@link AssemblingFile}.
	 */
	AssemblingFile getCurrentAssemblingFile();


	/**
	 * Returns a mutable {@link Map} with all global labels of this simulation..
	 *
	 * @return the {@link Map}.
	 */
	Map<String, Integer> getGlobalLabels();

	/**
	 * Indicates that the given label is a global label.
	 *
	 * @param executingLine the line the executes this code. Used for exceptions.
	 * @param label         the label.
	 * @throws AssemblerException if two local labels
	 *                            in different files but with the same name as the given label exists.
	 */
	void setAsGlobalLabel(int executingLine, String label);
}
