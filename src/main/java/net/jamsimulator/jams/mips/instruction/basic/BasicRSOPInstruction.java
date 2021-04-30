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

package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type SOP basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * an instruction code.
 */
public abstract class BasicRSOPInstruction<Inst extends AssembledInstruction> extends BasicInstruction<Inst> {

    private final int functionCode;
    private final int sopCode;

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array and an operation code.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param operationCode the operation code.
     * @param functionCode  the function code.
     */
    public BasicRSOPInstruction(String mnemonic, ParameterType[] parameters, int operationCode,
                                int functionCode, int sopCode) {
        super(mnemonic, parameters, operationCode);
        this.functionCode = functionCode;
        this.sopCode = sopCode;
    }

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array and an operation code.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param operationCode the operation code.
     * @param functionCode  the function code.
     */
    public BasicRSOPInstruction(String mnemonic, InstructionParameterTypes parameters, int operationCode,
                                int functionCode, int sopCode) {
        super(mnemonic, parameters, operationCode);
        this.functionCode = functionCode;
        this.sopCode = sopCode;
    }

    @Override
    public boolean match(int instructionCode) {
        int functionCode = instructionCode & AssembledRSOPInstruction.FUNCTION_CODE_MASK;
        int sopCode = instructionCode >> AssembledRSOPInstruction.SOP_SHIFT & AssembledRSOPInstruction.SOP_MASK;
        return super.match(instructionCode) && functionCode == this.functionCode && sopCode == this.sopCode;
    }

    /**
     * Returns the function code of the instruction.
     *
     * @return the function code.
     */
    public int getFunctionCode() {
        return functionCode;
    }

    /**
     * Returns the sop code of this instruction.
     *
     * @return the sop code.
     */
    public int getSopCode() {
        return sopCode;
    }
}
