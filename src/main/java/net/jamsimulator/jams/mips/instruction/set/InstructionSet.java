package net.jamsimulator.jams.mips.instruction.set;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledPCRELInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an instruction set. An instruction set stores a collection of instruction and it's
 * used in compile time to parse the code into {@link CompiledInstruction}s.
 * <p>
 * There may be several {@link InstructionSet} instances in the same runtime. The default one is located
 * inside the main class {@link net.jamsimulator.jams.Jams}.
 * <p>
 * Two {@link BasicInstruction} with the same operation code and function code (if present)
 * inside the same instruction set will produce an <b>unpredictable</b> behavior.
 *
 * @see Instruction
 * @see BasicInstruction
 */
public class InstructionSet {

	private static final CompatibleInstructionComparator COMPARATOR = new CompatibleInstructionComparator();

	private Set<Instruction> instructions;

	public InstructionSet(boolean loadDefaultBasics, boolean loadDefaultPseudo, boolean loadPluginInstructions) {
		instructions = new HashSet<>();
		if (loadDefaultBasics) {
			instructions.addAll(DefaultInstructions.basicInstructions);
		}
		if (loadDefaultPseudo) {
			instructions.addAll(DefaultInstructions.pseudoInstructions);
		}
		if (loadPluginInstructions) {
			//TOdo plugins
		}
	}

	/**
	 * Returns the {@link Instruction} that matches the given mnemonic and parameter types, if present.
	 *
	 * @param mnemonic   the given mnemonic.
	 * @param parameters the given parameters.
	 * @return the {@link Instruction}, if present.
	 * @throws NullPointerException if the mnemonic, the parameters array or any parameter are null.
	 */
	public Optional<Instruction> getInstruction(String mnemonic, ParameterType[] parameters) {
		Validate.notNull(mnemonic, "The given mnemonic cannot be null!");
		Validate.notNull(parameters, "The given parameters array cannot be null!");
		Validate.hasNoNulls(parameters, "One of the parameter types is null!");
		return instructions.stream().filter(target -> target.match(mnemonic, parameters)).findFirst();
	}

	/**
	 * Returns the best {@link Instruction} that is compatible with a combination of the given parameters.
	 * The possible combinations are represented by an array of lists. Each list has the possible combinations
	 * for the parameter in the same index.
	 * <p>
	 * This method will return the best option using a comparator that uses {@link ParameterType#ordinal()} to
	 * compare compatible instructions. {@link BasicInstruction}s have priority over {@link PseudoInstruction}s.
	 *
	 * @param mnemonic   the mnemonic of the instruction.
	 * @param parameters the given possible parameter types.
	 * @return the best {@link Instruction}, if present.
	 */
	public Optional<Instruction> getBestCompatibleInstruction(String mnemonic, List<ParameterType>[] parameters) {
		Validate.notNull(mnemonic, "Mnemonic cannot be null!");
		Validate.notNull(parameters, "Parameters cannot be null!");
		Validate.hasNoNulls(parameters, "Parameters cannot have null lists!");
		List<Instruction> compatibleInstructions = instructions.stream()
				.filter(target -> target.match(mnemonic, parameters)).collect(Collectors.toList());
		if (compatibleInstructions.isEmpty()) return Optional.empty();
		compatibleInstructions.sort(COMPARATOR);
		return Optional.of(compatibleInstructions.get(0));
	}

	/**
	 * Returns the first {@link BasicInstruction} that matches the given instruction code, if present.
	 *
	 * @param instructionCode the given instruction code.
	 * @return the {@link BasicInstruction}, if present.
	 */
	public Optional<BasicInstruction> getInstructionByInstructionCode(int instructionCode) {
		int operationCode = instructionCode >>> CompiledInstruction.OPERATION_CODE_SHIFT;
		int functionCode = instructionCode & CompiledRInstruction.FUNCTION_CODE_MASK;
		int fmtSub = instructionCode >> CompiledRFPUInstruction.FMT_SHIFT & CompiledRFPUInstruction.FMT_MASK;
		int pcRel = instructionCode >> CompiledPCRELInstruction.PCREL_SHIFT & CompiledPCRELInstruction.PCREL_MASK;
		return getInstructionByOperationAndFunctionCode(operationCode, functionCode, fmtSub, pcRel);
	}

	/**
	 * Returns the first {@link BasicInstruction} that matches the given operation code. If there are several
	 * instructions with the given operation code but with different function codes the method will
	 * return the one with the function code 0.
	 *
	 * @param operationCode the given operation code.
	 * @return the {@link BasicInstruction}, if present.
	 */
	public Optional<BasicInstruction> getInstructionByOperationCode(int operationCode) {
		return instructions.stream().filter(target -> target instanceof BasicInstruction)
				.map(target -> (BasicInstruction) target)
				.filter(target -> target.match(operationCode)).findFirst();
	}

	/**
	 * Returns the first {@link BasicInstruction} that matches the given operation code, the given function code (if found) and
	 * the given fmt or subcode (if found). If the instruction doesn't have an function code the given one will be
	 * ignored.
	 *
	 * @param operationCode the given operation code.
	 * @param functionCode  the given function code.
	 * @param fmtSub        the given ftm or subcode.
	 * @return the {@link BasicInstruction}, if present.
	 */
	public Optional<BasicInstruction> getInstructionByOperationAndFunctionCode(int operationCode, int functionCode, int fmtSub, int pcRel) {
		return instructions.stream().filter(target -> target instanceof BasicInstruction)
				.map(target -> (BasicInstruction) target)
				.filter(target -> target.match(operationCode, functionCode, fmtSub, pcRel)).findFirst();
	}


	/**
	 * Registers an instruction to the instruction set. If an instruction with the same
	 * mnemonic and parameters is registered in this instruction set the given instruction
	 * will not be added and the method will return {@code false}.
	 * <p>
	 * Remember that two {@link BasicInstruction} with the same operation code and function code (if present)
	 * inside the same instruction set will produce an <b>unpredictable</b> behavior.
	 * <p>
	 * If you're not sure whether an instruction with the same operation and instruction codes
	 * you can use {@link #getInstructionByInstructionCode(int)} and
	 * {@link #getInstructionByOperationAndFunctionCode(int, int, int, int)}
	 *
	 * @param instruction the instruction to register.
	 * @return whether the instruction was registered.
	 */
	public boolean registerInstruction(Instruction instruction) {
		Validate.notNull(instruction, "The given instruction cannot be null!");
		return instructions.add(instruction);
	}

	private static class CompatibleInstructionComparator implements Comparator<Instruction> {

		@Override
		public int compare(Instruction o1, Instruction o2) {
			//Remember: if negative, o1 has priority.

			//If one of them is a basic instruction, it has priority.
			if (o1 instanceof BasicInstruction && o2 instanceof PseudoInstruction)
				return -1;
			if (o1 instanceof PseudoInstruction && o2 instanceof BasicInstruction)
				return 1;

			//They must have the same length.
			ParameterType[] t1 = o1.getParameters();
			ParameterType[] t2 = o2.getParameters();

			int difference;
			//The last parameter has the biggest priority.
			for (int i = t1.length - 1; i >= 0; i--) {
				difference = t1[i].ordinal() - t2[i].ordinal();
				if (difference != 0) return difference;
			}

			return 0;
		}

	}
}
