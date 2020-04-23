package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionBcL extends PseudoInstruction {

	public static final String NAME = InstructionBc.NAME;
	public static final String MNEMONIC = InstructionBc.MNEMONIC;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.LABEL};
	private static final ParameterType[] BASIC_PARAMETER_TYPES = new ParameterType[]{ParameterType.SIGNED_32_BIT};

	public PseudoInstructionBcL() {
		super(NAME, MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 1;
	}

	@Override
	public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
		int offset = parameters[0].getLabelValue() - address - 4;
		offset >>= 2;

		Instruction bc = set.getInstruction(InstructionBc.MNEMONIC, BASIC_PARAMETER_TYPES).orElse(null);
		if (!(bc instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionBc.MNEMONIC + "' not found.");

		ParameterParseResult[] newParameters = new ParameterParseResult[]{
				ParameterParseResult.builder().immediate(offset).build()
		};

		return new AssembledInstruction[]{((BasicInstruction) bc).assembleBasic(newParameters, this)};
	}
}
