package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeq;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionBeqRRL extends PseudoInstruction {

	public static final String NAME = InstructionBeq.NAME;
	public static final String MNEMONIC = InstructionBeq.MNEMONIC;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.LABEL};
	private static final ParameterType[] BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public PseudoInstructionBeqRRL() {
		super(NAME, MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 1;
	}

	@Override
	public CompiledInstruction[] compile(InstructionSet set, int address, ParameterParseResult[] parameters) {
		int offset = parameters[2].getLabelValue() - address - 4;
		offset >>= 2;

		Instruction beq = set.getInstruction(MNEMONIC, BASIC_PARAMETER_TYPES).orElse(null);
		if (!(beq instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionBeq.MNEMONIC + "' not found.");

		ParameterParseResult[] newParameters = new ParameterParseResult[]{
				parameters[0], parameters[1],
				ParameterParseResult.builder().immediate(offset).build()
		};

		return new CompiledInstruction[]{((BasicInstruction) beq).compileBasic(newParameters, this)};
	}
}
