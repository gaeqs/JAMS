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
 * Represents a compiled I-Type Off9 instruction. An I-Type Off9 instruction is composed of a function code,
 * an 9-bit offset, one target register, one base register and one operation code.
 */
public abstract class AssembledI9Instruction extends AssembledInstruction {

    /**
     * The mask used by the function operation code.
     */
    public static final int FUNCTION_CODE_MASK = 0X3F;

    /**
     * The shift used by the offset.
     */
    public static final int OFFSET_SHIFT = 7;

    /**
     * The mask used by the offset after the shift.
     */
    public static final int OFFSET_MASK = 0x1FF;

    /**
     * The shift used by the target register.
     */
    public static final int TARGET_REGISTER_SHIFT = 16;

    /**
     * The mask used by the target register after the shift.
     */
    public static final int TARGET_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the base register.
     */
    public static final int BASE_REGISTER_SHIFT = 21;

    /**
     * The mask used by the base register after the shift.
     */
    public static final int BASE_REGISTER_MASK = 0x1F;

    /**
     * Creates a compiled I-Type Off9 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledI9Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }

    /**
     * Creates a compiled I-Type Off9 instruction using an operation code, a base register, a target register, an offset, a function code,
     * an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode  the operation code.
     * @param baseRegister   the base register .
     * @param targetRegister the target register.
     * @param offset         the immediate.
     * @param functionCode   the function code.
     * @param origin         the origin instruction.
     * @param basicOrigin    the origin basic instruction.
     */
    public AssembledI9Instruction(int operationCode, int baseRegister, int targetRegister, int offset, int functionCode, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, baseRegister, targetRegister, offset, functionCode), origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, base register, target register offset and function code.
     *
     * @param operationCode  the operation code.
     * @param baseRegister   the base register.
     * @param targetRegister the target register.
     * @param offset         the offset.
     * @param functionCode   the function operation code.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int baseRegister, int targetRegister, int offset, int functionCode) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (baseRegister & BASE_REGISTER_MASK) << BASE_REGISTER_SHIFT;
        value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
        value += (offset & OFFSET_MASK) << OFFSET_SHIFT;
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
     * Returns the offset value of the instruction as an unsigned 9-bit number.
     * For a signed version of this value see {@link #getOffsetAsSigned()}.
     *
     * @return the unsigned 9-bit immediate.
     */
    public int getOffset() {
        return value << OFFSET_SHIFT & OFFSET_MASK;
    }

    /**
     * Returns the offset value of the instruction as a signed 9-bit number.
     * For an unsigned version of this value see {@link #getOffset()}.
     *
     * @return the signed 9-bit immediate.
     */
    public int getOffsetAsSigned() {
        return (short) getOffset();
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
