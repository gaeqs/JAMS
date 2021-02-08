package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.*;
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
