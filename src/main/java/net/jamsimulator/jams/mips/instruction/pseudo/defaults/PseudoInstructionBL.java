package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeq;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionBL extends PseudoInstruction {

	public static final String NAME = "Unconditional branch";
	public static final String MNEMONIC = "b";

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.LABEL};

	private static final ParameterType[] BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};
	private static final ParameterParseResult ZERO = ParameterParseResult.builder().register(0).build();

	public PseudoInstructionBL() {
		super(InstructionBeq.NAME, InstructionBeq.MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 1;
	}

	@Override
	public CompiledInstruction[] compile(InstructionSet set, int address, ParameterParseResult[] parameters) {
		int offset = address - parameters[0].getLabelValue() - 4;
		offset >>= 2;

		Instruction beq = set.getInstruction(InstructionBeq.MNEMONIC, BASIC_PARAMETER_TYPES).orElse(null);
		if (!(beq instanceof BasicInstruction))
			throw new CompilerException("Basic instruction '" + InstructionBeq.MNEMONIC + "' not found.");

		ParameterParseResult[] newParameters = new ParameterParseResult[]{
				ZERO, ZERO, ParameterParseResult.builder().immediate(offset).build()
		};

		return new CompiledInstruction[]{((BasicInstruction) beq).compileBasic(newParameters, this)};
	}
}
