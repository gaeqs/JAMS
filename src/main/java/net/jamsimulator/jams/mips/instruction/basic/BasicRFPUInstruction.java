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

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.apu.APUType;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents an R-Type FPU basic instruction. This subclass adds the FMT
 * of the instruction, allowing the simulator to find this instruction based on an FMT.
 */
public abstract class BasicRFPUInstruction<Inst extends AssembledInstruction> extends BasicRInstruction<Inst> {

    private final int fmtCode;

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array, an operation code,
     * a function code and an operand type format specifier .
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param apuType       the type of the APU where this instruction will be executed.
     * @param operationCode the operation code.
     * @param functionCode  the function code.
     * @param fmtCode       the operand type format specifier.
     */
    public BasicRFPUInstruction(String mnemonic, ParameterType[] parameters, APUType apuType, int operationCode,
                                int functionCode, int fmtCode) {
        super(mnemonic, parameters, apuType, operationCode, functionCode);
        this.fmtCode = fmtCode;
    }

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array, an operation code,
     * a function code and an operand type format specifier .
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param apuType       the type of the APU where this instruction will be executed.
     * @param operationCode the operation code.
     * @param functionCode  the function code.
     * @param fmtCode       the operand type format specifier.
     */
    public BasicRFPUInstruction(String mnemonic, InstructionParameterTypes parameters, APUType apuType, int operationCode,
                                int functionCode, int fmtCode) {
        super(mnemonic, parameters, apuType, operationCode, functionCode);
        this.fmtCode = fmtCode;
    }

    @Override
    public boolean match(int instructionCode) {
        return super.match(instructionCode) &&
                ((instructionCode >> AssembledRFPUInstruction.FMT_SHIFT) & AssembledRFPUInstruction.FMT_MASK) == fmtCode;
    }

    /**
     * Returns the operand type format specifier of the instruction.
     *
     * @return the format specifier.
     */
    public int getFmtCode() {
        return fmtCode;
    }
}
