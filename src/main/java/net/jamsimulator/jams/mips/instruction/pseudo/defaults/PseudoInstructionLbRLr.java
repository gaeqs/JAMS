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

package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddu;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAui;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionLb;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionLbRLr extends PseudoInstruction {

    public static final String MNEMONIC = InstructionLb.MNEMONIC;

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.REGISTER, ParameterType.LABEL_REGISTER_SHIFT);

    public PseudoInstructionLbRLr() {
        super(MNEMONIC, PARAMETER_TYPES);
    }

    @Override
    public int getInstructionAmount(String[] parameters) {
        return 3;
    }

    @Override
    public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
        var instructions = instructions(set, InstructionAui.class, InstructionAddu.class, InstructionLb.class);

        int saveAddress = parameters[1].getLabelValue();

        var aui = parameters(AT, ZERO, immediate(upper(saveAddress)));
        var addu = parameters(AT, AT, register(parameters[1].getRegister()));
        var lb = parameters(parameters[0], registerImmediate(1, lower(saveAddress)));

        return assemble(instructions, aui, addu, lb);
    }
}
