package net.jamsimulator.jams.mips.compiler;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.List;
import java.util.Map;

/**
 * Represents a compiler. A compile transforms a list of files into a {@link Simulation}.
 * To create a simulation you must use the method {@link #compile()} before.
 * <p>
 * Each created {@link Simulation} will have a copy of this compiler's memory and registers.
 */
public interface Compiler {

	/**
	 * Sets the data to compile.
	 *
	 * @param data the data.
	 */
	void setData(List<List<String>> data);

	/**
	 * Compiles the program.
	 *
	 * @throws net.jamsimulator.jams.mips.compiler.exception.CompilerException if any compilation exception occurs.
	 * @throws IllegalStateException                                           if the compiler has already compiled its code
	 *                                                                         or if the compiler has no data.
	 */
	void compile();

	/**
	 * Creates a new {@link Simulation} using the compiled data of this compiler.
	 * <p>
	 * The {@link Simulation}'s {@link Memory} and {@link RegisterSet} is a copy of this compiler's data.
	 * Two {@link Simulation}s created by this method will have different memories and registers.
	 *
	 * @return the new {@link Simulation}.
	 * @throws IllegalStateException whether the compiler is not compiled.
	 * @see #compile()
	 */
	Simulation createSimulation();

	/**
	 * Returns whether this compiler has compiled its code.
	 *
	 * @return whether this compiler has compiled its code.
	 * @see #compile()
	 */
	boolean isCompiled();

	/**
	 * Returns this simulation's {@link RegisterSet}.
	 * You can modify this {@link RegisterSet}.
	 *
	 * @return the {@link RegisterSet}.
	 */
	RegisterSet getRegisterSet();

	/**
	 * Returns this simulation's {@link Memory}.
	 * You can modify this {@link Memory}.
	 *
	 * @return the {@link Memory}.
	 */
	Memory getMemory();

	/**
	 * Gets this compiler's data.
	 *
	 * @return the {@link CompilerData}.
	 */
	CompilerData getCompilerData();

	/**
	 * Gets the file that this compiler is currently compiling.
	 * If the compiler has finished, this method will return the last {@link CompilingFile}.
	 *
	 * @return the {@link CompilingFile}.
	 */
	CompilingFile getCurrentCompilingFile();


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
	 * @throws net.jamsimulator.jams.mips.compiler.exception.CompilerException if two local labels
	 *                                                                         in different files but with the same name as the given label exists.
	 */
	void setAsGlobalLabel(int executingLine, String label);

	/**
	 * Adds the given global label.
	 *
	 * @param executingLine the line the executes this code. Used for exceptions.
	 * @param label         the label.
	 * @param value         the label value.
	 * @throws net.jamsimulator.jams.mips.compiler.exception.CompilerException if the global label or a local label with the
	 *                                                                         same name already exist.
	 */
	void addGlobalLabel(int executingLine, String label, int value);
}
