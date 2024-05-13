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

package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * Represents a compiled REGIMM instruction. A REGIMM instruction is composed of an 16-bit immediate,
 * one function code, one source register and one operation code.
 */
public class AssembledRIInstruction extends AssembledInstruction {

    /**
     * The mask used by the immediate.
     */
    public static final int IMMEDIATE_MASK = 0xFFFF;

    /**
     * The shift used by the function code.
     */
    public static final int FUNCTION_CODE_SHIFT = 16;

    /**
     * The mask used by the function code after the shift.
     */
    public static final int FUNCTION_CODE_MASK = 0x1F;

    /**
     * The shift used by the source register.
     */
    public static final int SOURCE_REGISTER_SHIFT = 21;

    /**
     * The mask used by the source register after the shift.
     */
    public static final int SOURCE_REGISTER_MASK = 0x1F;

    /**
     * Creates a compiled REGIMM instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledRIInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }

    /**
     * Creates a compiled REGIMM instruction using an operation code, a source register, a function code, an immediate,
     * an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode  the operation code.
     * @param sourceRegister the source register.
     * @param functionCode   the function code.
     * @param immediate      the immediate.
     * @param origin         the origin instruction.
     * @param basicOrigin    the origin basic instruction.
     */
    public AssembledRIInstruction(int operationCode, int sourceRegister, int functionCode, int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, sourceRegister, functionCode, immediate), origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, source register, target register, and immediate.
     *
     * @param operationCode  the operation code.
     * @param sourceRegister the source register.
     * @param targetRegister the target register.
     * @param immediate      the immediate.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int sourceRegister, int targetRegister, int immediate) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
        value += (targetRegister & FUNCTION_CODE_MASK) << FUNCTION_CODE_SHIFT;
        value += immediate & IMMEDIATE_MASK;
        return value;
    }

    /**
     * Returns the immediate value of the instruction as an unsigned 16-bit number.
     * For a signed version of this value see {@link #getImmediateAsSigned()}.
     *
     * @return the unsigned 16-bit immediate.
     */
    public int getImmediate() {
        return value & IMMEDIATE_MASK;
    }

    /**
     * Returns the immediate value of the instruction as a signed 16-bit number.
     * For an unsigned version of this value see {@link #getImmediate()}.
     *
     * @return the signed 16-bit immediate.
     */
    public int getImmediateAsSigned() {
        return (short) getImmediate();
    }

    /**
     * Returns the function code of this instruction.
     *
     * @return the function code instruction.
     */
    public int getFunctionCode() {
        return value >> FUNCTION_CODE_SHIFT & FUNCTION_CODE_MASK;
    }

    /**
     * Returns the source register of this instruction-
     *
     * @return the source register.
     */
    public int getSourceRegister() {
        return value >> SOURCE_REGISTER_SHIFT & SOURCE_REGISTER_MASK;
    }

    @Override
    public String parametersToString(String registersStart) {
        return registersStart + getSourceRegister() + ", 0x" +
                StringUtils.addZeros(Integer.toHexString(getImmediate()), 4);
    }

}
