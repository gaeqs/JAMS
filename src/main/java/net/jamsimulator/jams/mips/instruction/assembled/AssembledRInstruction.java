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
 * Represents a compiled R-Type instruction. A R-Type instruction is composed of a function code,
 * a "shamt" (unsigned 5-bit immediate), two source registers (source and target), one destination register
 * and one operation code.
 */
public abstract class AssembledRInstruction extends AssembledInstruction {

    /**
     * The mask used by the function code.
     */
    public static final int FUNCTION_CODE_MASK = 0X3F;

    /**
     * The shift used by the shift amout.
     */
    public static final int SHIFT_AMOUNT_SHIFT = 6;

    /**
     * The mask used by the shift amount after the shift.
     */
    public static final int SHIFT_AMOUNT_MASK = 0x1F;

    /**
     * The shift used by the destination register.
     */
    public static final int DESTINATION_REGISTER_SHIFT = 11;

    /**
     * The mask used by the destination register after the shift.
     */
    public static final int DESTINATION_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the source register.
     */
    public static final int SOURCE_REGISTER_SHIFT = 16;

    /**
     * The mask used by the source register after the shift.
     */
    public static final int SOURCE_REGISTER_MASK = 0x1F;

    /**
     * The shift used by the target register.
     */
    public static final int TARGET_REGISTER_SHIFT = 21;

    /**
     * The mask used by the target register after the shift.
     */
    public static final int TARGET_REGISTER_MASK = 0x1F;


    /**
     * Creates a compiled R-Type instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param value       the value of the instruction.
     * @param origin      the origin instruction.
     * @param basicOrigin the origin basic instruction.
     */
    public AssembledRInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(value, origin, basicOrigin);
    }


    /**
     * Creates a compiled R-Type instruction using an operation code, a source register, a target register, a destination register,
     * a shift amount 5-bit immediate, a function code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
     *
     * @param operationCode       the operation code.
     * @param sourceRegister      the source register.
     * @param targetRegister      the target register.
     * @param destinationRegister the destination register.
     * @param shiftAmount         the shift amount immediate value.
     * @param functionCode        the function code.
     * @param origin              the origin instruction.
     * @param basicOrigin         the origin basic instruction.
     */
    public AssembledRInstruction(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
                                 int shiftAmount, int functionCode, Instruction origin, BasicInstruction<?> basicOrigin) {
        super(calculateValue(operationCode, sourceRegister, targetRegister, destinationRegister, shiftAmount, functionCode),
                origin, basicOrigin);
    }

    /**
     * Calculates the integer representing the instruction using the given
     * operation code, source register, target register, destination register, shift amount and function code.
     *
     * @param operationCode       the operation code.
     * @param sourceRegister      the source register.
     * @param targetRegister      the target register.
     * @param destinationRegister the destination register.
     * @param shiftAmount         the shift amount.
     * @param functionCode        the function code.
     * @return the integer representing the instruction.
     */
    static int calculateValue(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
                              int shiftAmount, int functionCode) {
        int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
        value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
        value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
        value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
        value += (shiftAmount & SHIFT_AMOUNT_MASK) << SHIFT_AMOUNT_SHIFT;
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
    public int getShiftAmount() {
        return value >>> SHIFT_AMOUNT_SHIFT & SHIFT_AMOUNT_MASK;
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
