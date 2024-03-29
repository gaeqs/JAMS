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
 * Represents a compiled Register-Immediate FPU instruction. Register-Immediate FPU instruction is composed of an 11-bit immediate,
 * one source register, one target register, one subcode and one operation code.
 */
public abstract class AssembledRIFPUInstruction extends AssembledInstruction {

    /**
     * The mask used by the immediate.
     */
    public static final int IMMEDIATE_MASK = 0x7FF;

    /**
     * The shift used by the source register.
     */
    public static final int SOURCE_REGISTER_SHIFT = 11;

    /**
     * The mask used by the source register after the shift.
     */
    public static final int SOURCE_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the target register.
     */
    public static final int TARGET_REGISTER_SHIFT = 16;

    /**
     * The mask used by the target register after the shift.
     */
    public static final int TARGET_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the subcode.
     */
    public static final int SUBCODE_SHIFT = 21;

    /**
     * The mask used by the subcode after the shift.
     */
    public static final int SUBCODE_MASK = 0x1F;

    /**
     * Creates a compiled Register-Immediate FPU instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledRIFPUInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }

    /**
     * Creates a compiled Register-Immediate FPU instruction using an operation code, a subcode, a target register, a source register,
     * an immediate, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode  the operation code.
     * @param subcode        the subcode.
     * @param targetRegister the target register.
     * @param immediate      the immediate.
     * @param origin         the origin instruction.
     * @param basicOrigin    the origin basic instruction.
     */
    public AssembledRIFPUInstruction(int operationCode, int subcode, int targetRegister, int sourceRegister,
                                     int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, subcode, targetRegister, sourceRegister, immediate), origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, subcode, target register, source register and immediate.
     *
     * @param operationCode  the operation code.
     * @param subcode        the subcode.
     * @param targetRegister the target register.
     * @param sourceRegister the source register.
     * @param immediate      the immediate.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int subcode, int targetRegister, int sourceRegister, int immediate) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (subcode & SUBCODE_MASK) << SUBCODE_SHIFT;
        value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
        value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
        value += immediate & IMMEDIATE_MASK;
        return value;
    }

    /**
     * Returns the immediate value of the instruction as an unsigned 11-bit number.
     * For a signed version of this value see {@link #getImmediateAsSigned()}.
     *
     * @return the unsigned 11-bit immediate.
     */
    public int getImmediate() {
        return value & IMMEDIATE_MASK;
    }

    /**
     * Returns the immediate value of the instruction as a signed 11-bit number.
     * For an unsigned version of this value see {@link #getImmediate()}.
     *
     * @return the signed 11-bit immediate.
     */
    public int getImmediateAsSigned() {
        return (short) getImmediate();
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
     * Returns the subcode of this instruction.
     *
     * @return the subcode.
     */
    public int getSubcode() {
        return value >> SUBCODE_SHIFT & SUBCODE_MASK;
    }

}
