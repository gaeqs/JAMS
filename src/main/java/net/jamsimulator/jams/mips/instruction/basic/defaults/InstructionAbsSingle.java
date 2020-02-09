package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.RFPUBasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAbsDouble;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAbsSingle;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAbsSingle extends RFPUBasicInstruction {

	public static final String NAME = "Absolute (single)";
	public static final String MNEMONIC = "abs.s";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10000;
	public static final int FUNCTION_CODE = 0b000101;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.FLOAT_REGISTER, ParameterType.FLOAT_REGISTER};

	public InstructionAbsSingle() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters) {
		return new CompiledInstructionAbsSingle(parameters[1].getRegister(), parameters[0].getRegister(), this, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAbsSingle(instructionCode, this, this);
	}
}
