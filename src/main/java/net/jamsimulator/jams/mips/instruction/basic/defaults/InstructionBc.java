package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBalc;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBc extends BasicInstruction {

	public static final String NAME = "Branch compact";
	public static final String MNEMONIC = "bc";
	public static final int OPERATION_CODE = 0b110010;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.SIGNED_32_BIT};

	public InstructionBc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBc(origin, this, parameters[0].getImmediate());
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBc(instructionCode, this, this);
	}
}
