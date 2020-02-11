package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAnd;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAnd extends BasicRInstruction {

	public static final String NAME = "And";
	public static final String MNEMONIC = "and";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b100100;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionAnd() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters) {
		return new CompiledInstructionAnd(parameters[1].getRegister(),
				parameters[2].getRegister(),
				parameters[0].getRegister(), this, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAnd(instructionCode, this, this);
	}
}
