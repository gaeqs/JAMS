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

package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a I-Type FPU basic instruction. This subclass adds the subcode
 * of the instruction, allowing the simulator to find this instruction based on
 * a subcode.
 */
public abstract class BasicIFPUInstruction<Inst extends AssembledInstruction> extends BasicInstruction<Inst> {

    private final int subcode;

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array, an operation code,
     * and a subcode.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param aluType       the type of the ALU where this instruction will be executed.
     * @param operationCode the operation code.
     * @param subcode       the subcode.
     */
    public BasicIFPUInstruction(String mnemonic, ParameterType[] parameters, ALUType aluType, int operationCode, int subcode) {
        super(mnemonic, parameters, aluType, operationCode);
        this.subcode = subcode;
    }

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array, an operation code,
     * and a subcode.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param aluType       the type of the ALU where this instruction will be executed.
     * @param operationCode the operation code.
     * @param subcode       the subcode.
     */
    public BasicIFPUInstruction(String mnemonic, InstructionParameterTypes parameters, ALUType aluType, int operationCode, int subcode) {
        super(mnemonic, parameters, aluType, operationCode);
        this.subcode = subcode;
    }


    @Override
    public boolean match(int instructionCode) {
        return super.match(instructionCode) &&
                ((instructionCode >> AssembledIFPUInstruction.BASE_REGISTER_SHIFT) & AssembledIFPUInstruction.BASE_REGISTER_MASK) == subcode;
    }

    /**
     * Returns the subcode.
     *
     * @return the subcode.
     */
    public int getSubcode() {
        return subcode;
    }
}
