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
 * Represents a compiled I-Type Imm21 instruction. An I-Type Imm21 instruction is composed of an 21-bit immediate,
 * one destination register and one operation code.
 */
public abstract class AssembledI21Instruction extends AssembledInstruction {

    /**
     * The mask used by the immediate.
     */
    public static final int IMMEDIATE_MASK = 0x1FFFFF;

    /**
     * The shift used by the destination register.
     */
    public static final int DESTINATION_REGISTER_SHIFT = 21;

    /**
     * The mask used by the destination register after the shift.
     */
    public static final int DESTINATION_REGISTER_MASK = 0x1F;

    /**
     * Creates a compiled I-Type Imm21 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledI21Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }

    /**
     * Creates a compiled I-Type Imm21 instruction using an operation code, a destination register, an immediate,
     * an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode       the operation code.
     * @param destinationRegister the destination.
     * @param immediate           the immediate.
     * @param origin              the origin instruction.
     * @param basicOrigin         the origin basic instruction.
     */
    public AssembledI21Instruction(int operationCode, int destinationRegister, int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, destinationRegister, immediate), origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, destination register and immediate.
     *
     * @param operationCode       the operation code.
     * @param destinationRegister the destination register.
     * @param immediate           the immediate.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int destinationRegister, int immediate) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
        value += immediate & IMMEDIATE_MASK;
        return value;
    }

    /**
     * Returns the immediate value of the instruction as an unsigned 21-bit number.
     * For a signed version of this value see {@link #getImmediateAsSigned()}.
     *
     * @return the unsigned 21-bit immediate.
     */
    public int getImmediate() {
        return value & IMMEDIATE_MASK;
    }

    /**
     * Returns the immediate value of the instruction as a signed 21-bit number.
     * For an unsigned version of this value see {@link #getImmediate()}.
     *
     * @return the signed 21-bit immediate.
     */
    public int getImmediateAsSigned() {
        final int SHIFT = 32 - 21;
        int immediate = getImmediate();
        immediate <<= SHIFT;
        immediate >>= SHIFT;
        return immediate;
    }

    /**
     * Returns the destination register of this instruction.
     *
     * @return the destination instruction.
     */
    public int getDestinationRegister() {
        return value >> DESTINATION_REGISTER_SHIFT & DESTINATION_REGISTER_MASK;
    }
}
