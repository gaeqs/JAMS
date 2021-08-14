/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.List;

/**
 * Represents a MIPS instruction. This instruction is used in compile time to compile a program.
 * Plugins may add more instructions to the simulator.
 * <p>
 * Instruction implementations must be immutable classes. The data of an instruction cannot change.
 * <p>
 * Several instructions with the same mnemonic but different parameters may coexist at the same time.
 * For example, these two instructions are different {@link Instruction} instances:
 * <p>
 * add $t0,$t1,$t2
 * <p>
 * add $t0,$t1,100
 * <p>
 * The first one is a basic instruction that has three registers as parameters, and the second one
 * is a pseudo instruction that has two registers and a 16-bit immediate as parameters.
 * <p>
 * Two instructions with the same mnemonic and parameter types cannot coexist in the simulator.
 * <p>
 * Base instructions and pseudo instructions are divided into two different classes,
 * BasicInstruction and PseudoInstruction.
 */
public interface Instruction {

    /**
     * Returns the name of the instruction. This is not the MIPS mnemonic, but a human-like name.
     * For example, the name for the "add" instruction may be "Addition".
     * <p>
     * This name depends on the current language of JAMS.
     * <p>
     * For the mnemonic of the instruction see {@link #getMnemonic()}.
     *
     * @return the human-like name.
     */
    String getName();

    /**
     * Returns the documentation of the instruction.
     * This string is an HTML-like formatted text containing a complete description of the instruction.
     * <p>
     * This documentation depends on the current language of JAMS.
     *
     * @return the documentation.
     */
    String getDocumentation();

    /**
     * Returns the MIPS mnemonic of the instruction. This is the static short MIPS name of the instruction,
     * and it's used to filter and check the instruction in compile time.
     * <p>
     * For a human-like name see {@link #getName()}.
     *
     * @return the MIPS mnemonic.
     */
    String getMnemonic();

    /**
     * Returns the parameter types of the instruction. These {@link ParameterType}s are used
     * to filter several instructions with the same mnemonic in compile time.
     * <p>
     * The returned array is a copy of the original one, so you cannot change the parameters of an instruction.
     *
     * @return the parameter types of the instruction.
     */
    ParameterType[] getParameters();

    /**
     * Returns whether this instruction has any parameter.
     *
     * @return whether this instruction has at least one parameter.
     */
    boolean hasParameters();

    /**
     * Returns whether the instruction matches the given mnemonic and parameter types.
     *
     * @param mnemonic   the given mnemonic.
     * @param parameters the given parameter types.
     * @return whether the instruction matches the given mnemonic and parameter types.
     */
    boolean match(String mnemonic, ParameterType[] parameters);

    /**
     * Returns whether the instruction matches the given mnemonic and parameter types.
     *
     * @param mnemonic   the given mnemonic.
     * @param parameters the given parameter types.
     * @return whether the instruction matches the given mnemonic and parameter types.
     */
    default boolean match(String mnemonic, InstructionParameterTypes parameters) {
        return match(mnemonic, parameters.getParameters());
    }

    /**
     * Returns whether the instruction matches the given mnemonic and a combination of the given parameter types.
     *
     * @param mnemonic   the given mnemonic.
     * @param parameters the given possible parameter types.
     * @return whether the instruction matches the given mnemonic and parameter types.
     */
    boolean match(String mnemonic, List<ParameterType>[] parameters);

    /**
     * Compiles the instruction using the given parameters.
     *
     * @param set        the instruction set used by the compiler. This is used by pseodu-instruction to get their basic instructions.
     * @param address    the address where this instruction is stored. This is used by pseudo-instructions.
     * @param parameters the parameters.
     * @return a {@link AssembledInstruction} array.
     */
    AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters);

}
