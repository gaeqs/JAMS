/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.mips.assembler;

import javafx.util.Pair;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.InstructionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

class MIPS32AssemblerInstruction {

    private final MIPS32AssemblerLine line;
    private final String mnemonic;
    private final String rawParameters;

    private final Instruction instruction;
    private final List<String> parameters;


    MIPS32AssemblerInstruction(MIPS32AssemblerLine line, String mnemonic, String rawParameters) {
        this.line = line;
        this.mnemonic = mnemonic;
        this.rawParameters = rawParameters;

        var set = line.getAssembler().getInstructionSet();
        var instructions = set.getInstructionByMnemonic(mnemonic);
        var pair = scanInstruction(
                line.getAssembler().getRegisters(),
                instructions,
                rawParameters
        );

        instruction = pair.getKey();
        parameters = pair.getValue();

        if (instruction == null) {
            throw new AssemblerException(line.getIndex(),
                    "Instruction " + mnemonic + " with the given parameters not found.\n" + rawParameters);
        }
    }

    public MIPS32AssemblerLine getLine() {
        return line;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getRawParameters() {
        return rawParameters;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public int getInstructionSize() {
        return instruction instanceof PseudoInstruction
                ? ((PseudoInstruction) instruction).getInstructionAmount(parameters) << 2
                : 4;
    }

    private static Pair<Instruction, List<String>> scanInstruction(
            Registers registers,
            Set<Instruction> instructions,
            String rawParameters
    ) {
        var parametersReference = new AtomicReference<List<String>>();
        var instruction = InstructionUtils
                .getBestInstruction(instructions, parametersReference, registers, rawParameters).orElse(null);
        var parameters = parametersReference.get();
        return new Pair<>(instruction, parameters);
    }
}
