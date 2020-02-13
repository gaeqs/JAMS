package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc1eqz;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionBc1eqzL extends PseudoInstruction {

	public static final String NAME = InstructionBc1eqz.NAME;
	public static final String MNEMONIC = InstructionBc1eqz.MNEMONIC;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.LABEL};
	private static final ParameterType[] BASIC_PARAMETER_TYPES = new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.SIGNED_16_BIT};

	public PseudoInstructionBc1eqzL() {
		super(NAME, MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 1;
	}

	@Override
	public CompiledInstruction[] compile(InstructionSet set, int address, ParameterParseResult[] parameters) {
		int offset = parameters[1].getLabelValue() - address - 4;
		offset >>= 2;

		Instruction basic = set.getInstruction(InstructionBc1eqz.MNEMONIC, BASIC_PARAMETER_TYPES).orElse(null);
		if (!(basic instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionBc1eqz.MNEMONIC + "' not found.");

		ParameterParseResult[] newParameters = new ParameterParseResult[]{
				parameters[0],
				ParameterParseResult.builder().immediate(offset).build()
		};

		return new CompiledInstruction[]{((BasicInstruction) basic).compileBasic(newParameters, this)};
	}
}
