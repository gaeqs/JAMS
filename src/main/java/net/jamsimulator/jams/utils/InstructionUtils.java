package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.*;

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


	public static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions, List<String> parameters,
															Registers registers, String rawParameters) {
		return getBestInstruction(instructions, parameters, registers, null, rawParameters);
	}

	public static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions, List<String> parameters,
															RegistersBuilder builder, String rawParameters) {
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

		parameters.clear();

		for (int i = 0; i < iParameters.length && valid; i++) {
			currentType = iParameters[i];
			if (i + 1 == iParameters.length) {

				if (builder == null) {
					valid = ParameterType.getCompatibleParameterTypes(sParameters, registers).contains(currentType);
				} else {
					valid = ParameterType.getCompatibleParameterTypes(sParameters, builder).contains(currentType);
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
						valid = ParameterType.getCompatibleParameterTypes(currentSParameter, registers).contains(currentType);
					} else {
						valid = ParameterType.getCompatibleParameterTypes(currentSParameter, builder).contains(currentType);
					}

					parameters.add(currentSParameter);
				}
			}
		}
		return valid;
	}

	private static Optional<Instruction> getBestInstruction(Collection<Instruction> instructions, List<String> parameters,
															Registers registers, RegistersBuilder builder, String rawParameters) {
		List<List<String>> candidatesParameters = new ArrayList<>();
		List<Instruction> candidates = new ArrayList<>();

		for (var current : instructions) {
			if (InstructionUtils.validateInstruction(parameters, registers, builder, rawParameters, current)) {
				candidates.add(current);
				candidatesParameters.add(new LinkedList<>(parameters));
			}
		}

		if (candidates.isEmpty()) {
			return Optional.empty();
		}

		parameters.clear();

		if (candidates.size() == 1) {
			parameters.addAll(candidatesParameters.get(0));
			return Optional.ofNullable(candidates.get(0));
		} else {
			int i = InstructionSet.bestInstructionOf(candidates);
			parameters.addAll(candidatesParameters.get(i % candidates.size()));
			return Optional.ofNullable(candidates.get(i % candidates.size()));
		}
	}
}
