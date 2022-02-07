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

package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAui;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMtc1;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionOri;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionLidRD extends PseudoInstruction {

    public static final String MNEMONIC = "lid";

    public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.EVEN_FLOAT_REGISTER, ParameterType.DOUBLE);

    public PseudoInstructionLidRD() {
        super(MNEMONIC, PARAMETER_TYPES);
    }

    @Override
    public int getInstructionAmount(String[] parameters) {
        return 6;
    }

    @Override
    public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
        var instructions = instructions(set,
                InstructionAui.class, InstructionOri.class, InstructionMtc1.class,
                InstructionAui.class, InstructionOri.class, InstructionMtc1.class);

        long load = (long) parameters[1].getUndefined();
        int low = (int) load;
        int high = (int) (load >> 32);

        var aui1 = parameters(AT, ZERO, immediate(upper(low)));
        var ori1 = parameters(AT, AT, immediate(lower(low)));
        var mtc11 = parameters(AT, parameters[0]);
        var aui2 = parameters(AT, ZERO, immediate(upper(high)));
        var ori2 = parameters(AT, AT, immediate(lower(high)));
        var mtc12 = parameters(AT, register(parameters[1].getRegister() + 1));
        return assemble(instructions, aui1, ori1, mtc11, aui2, ori2, mtc12);
    }
}
