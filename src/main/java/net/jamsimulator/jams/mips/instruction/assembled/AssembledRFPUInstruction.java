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

/**
 * Represents a compiled R-Type FPU instruction. A R-Type FPU instruction is composed of a function code,
 * two source registers (source and target), one destination register, one operand type format specifier (FMT)
 * and one operation code.
 */
public abstract class AssembledRFPUInstruction extends AssembledInstruction {

    public static final int FUNCTION_CODE_MASK = 0X3F;
    public static final int DESTINATION_REGISTER_SHIFT = 6;
    public static final int DESTINATION_REGISTER_MASK = 0x1F;
    public static final int SOURCE_REGISTER_SHIFT = 11;
    public static final int SOURCE_REGISTER_MASK = 0x1F;
    public static final int TARGET_REGISTER_SHIFT = 16;
    public static final int TARGET_REGISTER_MASK = 0x1F;
    public static final int FMT_SHIFT = 21;
    public static final int FMT_MASK = 0x1F;

    /**
     * Creates a compiled I instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledRFPUInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }


    /**
     * Creates a compiled I instruction using an operation code, a source register, a target register, a destination register,
     * a shift amount 5-bit immediate, a function code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode       the operation code.
     * @param fmt                 the operand type format specifier.
     * @param targetRegister      the target register.
     * @param sourceRegister      the source register.
     * @param destinationRegister the destination register.
     * @param functionCode        the function code.
     * @param origin              the origin instruction.
     * @param basicOrigin         the origin basic instruction.
     */
    public AssembledRFPUInstruction(int operationCode, int fmt, int targetRegister, int sourceRegister, int destinationRegister,
                                    int functionCode, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, fmt, targetRegister, sourceRegister, destinationRegister, functionCode),
                origin, basicOrigin);
    }

    static int calculateValue(int operationCode, int fmt, int targetRegister, int sourceRegister,
                              int destinationRegister, int functionCode) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (fmt & FMT_MASK) << FMT_SHIFT;
        value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
        value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
        value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
        value += functionCode & FUNCTION_CODE_MASK;
        return value;
    }

    /**
     * Returns the function code of the instruction.
     *
     * @return the function code.
     */
    public int getFunctionCode() {
        return value & FUNCTION_CODE_MASK;
    }

    /**
     * Returns the shift amount immediate of the instruction.
     *
     * @return the shift amount immediate.
     */
    public int getFMT() {
        return value >>> FMT_SHIFT & FMT_MASK;
    }

    /**
     * Returns the destination register of the instruction.
     *
     * @return the destination register.
     */
    public int getDestinationRegister() {
        return value >> DESTINATION_REGISTER_SHIFT & DESTINATION_REGISTER_MASK;
    }

    /**
     * Returns the target register of this instruction.
     *
     * @return the target instruction.
     */
    public int getTargetRegister() {
        return value >> TARGET_REGISTER_SHIFT & TARGET_REGISTER_MASK;
    }

    /**
     * Returns the source register of this instruction-
     *
     * @return the source register.
     */
    public int getSourceRegister() {
        return value >> SOURCE_REGISTER_SHIFT & SOURCE_REGISTER_MASK;
    }

}
