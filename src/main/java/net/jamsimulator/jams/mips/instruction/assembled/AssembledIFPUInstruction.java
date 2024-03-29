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
 * Represents a compiled I-Type FPU instruction. An I-Type FPU instruction is composed of an 16-bit immediate,
 * one target register, one base register and one operation code.
 */
public abstract class AssembledIFPUInstruction extends AssembledInstruction {

    /**
     * The mask used by the immediate value.
     */
    public static final int IMMEDIATE_MASK = 0xFFFF;

    /**
     * The shift used by the target register value.
     */
    public static final int TARGET_REGISTER_SHIFT = 16;

    /**
     * The mask used by the target register value after the shift.
     */
    public static final int TARGET_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the base register value.
     */
    public static final int BASE_REGISTER_SHIFT = 21;

    /**
     * The mask used by the base register value after the shift.
     */
    public static final int BASE_REGISTER_MASK = 0x1F;

    /**
     * Creates a compiled I-Type FPU instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledIFPUInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }

    /**
     * Creates a compiled I-Type FPU instruction using an operation code, a base register, a target register, an immediate,
     * an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode  the operation code.
     * @param baseRegister   the base register .
     * @param targetRegister the target register.
     * @param immediate      the immediate.
     * @param origin         the origin instruction.
     * @param basicOrigin    the origin basic instruction.
     */
    public AssembledIFPUInstruction(int operationCode, int baseRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, baseRegister, targetRegister, immediate), origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, base register, target register and immediate.
     *
     * @param operationCode  the operation code.
     * @param baseRegister   the base register.
     * @param targetRegister the target register.
     * @param immediate      the immediate value.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int baseRegister, int targetRegister, int immediate) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (baseRegister & BASE_REGISTER_MASK) << BASE_REGISTER_SHIFT;
        value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
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
     * Returns the target register of this instruction.
     *
     * @return the target instruction.
     */
    public int getTargetRegister() {
        return value >> TARGET_REGISTER_SHIFT & TARGET_REGISTER_MASK;
    }

    /**
     * Returns the base register of this instruction-
     *
     * @return the base register.
     */
    public int getBaseRegister() {
        return value >> BASE_REGISTER_SHIFT & BASE_REGISTER_MASK;
    }

}
