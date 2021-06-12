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

package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InstructionUtils {


    /**
     * Returns whether the given instruction is compatible with the given raw parameters.
     * <p>
     * If the instruction is valid, the split parameters will be stored inside the set 'parameters'.
     *
     * @param parameters    the set where split parameters will be stored.
     * @param builder       the registers builder of the assembler.
     * @param rawParameters the raw parameters.
     * @param instruction   the instruction.
     * @return whether the instruction is valid.
     */
    public static boolean validateInstruction(List<String> parameters, RegistersBuilder builder, String rawParameters, Instruction instruction) {
        return validateInstruction(parameters, null, builder, rawParameters, instruction);
    }

    /**
     * Returns whether the given instruction is compatible with the given raw parameters.
     * <p>
     * If the instruction is valid, the split parameters will be stored inside the set 'parameters'.
     *
     * @param parameters    the set where split parameters will be stored.
     * @param registers     the register set of the assembler.
     * @param rawParameters the raw parameters.
     * @param instruction   the instruction.
     * @return whether the instruction is valid.
     */
    public static boolean validateInstruction(List<String> parameters, Registers registers, String rawParameters, Instruction instruction) {
        return validateInstruction(parameters, registers, null, rawParameters, instruction);
    }


    public static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions,
                                                           AtomicReference<List<String>> parameters,
                                                           Registers registers,
                                                           String rawParameters) {
        return getBestInstruction(instructions, parameters, registers, null, rawParameters);
    }

    public static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions,
                                                           AtomicReference<List<String>> parameters,
                                                           RegistersBuilder builder,
                                                           String rawParameters) {
        return getBestInstruction(instructions, parameters, null, builder, rawParameters);
    }

    private static boolean validateInstruction(List<String> parameters, Registers registers,
                                               RegistersBuilder builder, String rawParameters, Instruction instruction) {
        int index;
        String currentSParameter;
        String sParameters = rawParameters.trim();
        ParameterType[] iParameters = instruction.getParameters();
        ParameterType currentType;
        boolean valid = true;

        if (iParameters.length == 0 && !sParameters.isEmpty()) return false;

        for (int i = 0; i < iParameters.length && valid; i++) {
            currentType = iParameters[i];
            if (i + 1 == iParameters.length) {

                if (builder == null) {
                    valid = currentType.match(sParameters, registers);
                } else {
                    valid = currentType.match(sParameters, builder);
                }

                parameters.add(sParameters);
            } else {
                index = StringUtils.indexOf(sParameters, ' ', ',', '\t');
                if (index == -1) {
                    valid = false;
                } else {
                    currentSParameter = sParameters.substring(0, index);
                    sParameters = sParameters.substring(index + 1).trim();

                    if (builder == null) {
                        valid = currentType.match(currentSParameter, registers);
                    } else {
                        valid = currentType.match(currentSParameter, builder);
                    }

                    parameters.add(currentSParameter);
                }
            }
        }
        return valid;
    }

    private static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions,
                                                            AtomicReference<List<String>> parameters,
                                                            Registers registers,
                                                            RegistersBuilder builder,
                                                            String rawParameters) {
        Instruction best = null;

        for (var current : instructions) {
            if (best == null || InstructionSet.COMPARATOR.compare(best, current) > 0) {
                var list = new ArrayList<String>(current.getParameters().length);
                if (InstructionUtils.validateInstruction(list, registers, builder, rawParameters, current)) {
                    best = current;
                    parameters.set(list);
                }
            }
        }

        return Optional.ofNullable(best);
    }
}
