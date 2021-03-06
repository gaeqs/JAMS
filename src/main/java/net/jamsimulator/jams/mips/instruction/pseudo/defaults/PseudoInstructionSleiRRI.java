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

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddiu;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionOri;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSlt;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSubu;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionSleiRRI extends PseudoInstruction {


    public static final String NAME = "Set on lower than or equal immediate";
    public static final String MNEMONIC = "slei";

    private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

    private static final ParameterType[] ADDIU_PARAMETER_TYPES =
            new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

    private static final ParameterType[] SLT_PARAMETER_TYPES =
            new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

    private static final ParameterType[] ORI_BASIC_PARAMETER_TYPES =
            new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

    private static final ParameterType[] SUBU_BASIC_PARAMETER_TYPES =
            new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

    private static final ParameterParseResult ZERO = ParameterParseResult.builder().register(0).build();
    private static final ParameterParseResult AT = ParameterParseResult.builder().register(1).build();

    public PseudoInstructionSleiRRI() {
        super(MNEMONIC, PARAMETER_TYPES);
    }

    @Override
    public int getInstructionAmount(String[] parameters) {
        return 4;
    }

    @Override
    public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
        //Get instructions
        Instruction addiu = set.getInstruction(InstructionAddiu.MNEMONIC, ADDIU_PARAMETER_TYPES).orElse(null);
        if (!(addiu instanceof BasicInstruction))
            throw new AssemblerException("Basic instruction '" + InstructionAddiu.MNEMONIC + "' not found.");

        Instruction slt = set.getInstruction(InstructionSlt.MNEMONIC, SLT_PARAMETER_TYPES).orElse(null);
        if (!(slt instanceof BasicInstruction))
            throw new AssemblerException("Basic instruction '" + InstructionSlt.MNEMONIC + "' not found.");

        Instruction ori = set.getInstruction(InstructionOri.MNEMONIC, ORI_BASIC_PARAMETER_TYPES).orElse(null);
        if (!(ori instanceof BasicInstruction))
            throw new AssemblerException("Basic instruction '" + InstructionOri.MNEMONIC + "' not found.");

        Instruction subu = set.getInstruction(InstructionSubu.MNEMONIC, SUBU_BASIC_PARAMETER_TYPES).orElse(null);
        if (!(subu instanceof BasicInstruction))
            throw new AssemblerException("Basic instruction '" + InstructionSubu.MNEMONIC + "' not found.");

        //Get parameters
        ParameterParseResult[] addiuParameters = new ParameterParseResult[]{
                AT, ZERO, parameters[2]
        };

        ParameterParseResult[] sltParameters = new ParameterParseResult[]{
                parameters[0],
                AT,
                parameters[1]
        };

        ParameterParseResult[] oriParameters = new ParameterParseResult[]{
                AT,
                ZERO,
                ParameterParseResult.builder().immediate(1).build()
        };

        ParameterParseResult[] subuParameters = new ParameterParseResult[]{
                parameters[0],
                AT,
                parameters[0]
        };

        return new AssembledInstruction[]{
                ((BasicInstruction<?>) addiu).assembleBasic(addiuParameters, this),
                ((BasicInstruction<?>) slt).assembleBasic(sltParameters, this),
                ((BasicInstruction<?>) ori).assembleBasic(oriParameters, this),
                ((BasicInstruction<?>) subu).assembleBasic(subuParameters, this)};
    }
}
