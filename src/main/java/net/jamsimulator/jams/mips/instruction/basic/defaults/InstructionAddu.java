package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.RBasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAdd;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAddu;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAddu extends RBasicInstruction {

	public static final String NAME = "Addition without overflow";
	public static final String MNEMONIC = "addu";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b100001;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionAddu() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters) {
		return new CompiledInstructionAddu(parameters[1].getRegister(),
				parameters[2].getRegister(),
				parameters[0].getRegister(), this, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAddu(instructionCode, this, this);
	}
}