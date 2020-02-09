package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.RFPUBasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAbsDouble;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAddDouble;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAddDouble extends RFPUBasicInstruction {

	public static final String NAME = "Addition (double)";
	public static final String MNEMONIC = "add.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000000;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionAddDouble() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters) {
		return new CompiledInstructionAddDouble(parameters[2].getRegister(), parameters[1].getRegister(),
				parameters[0].getRegister(), this, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAddDouble(instructionCode, this, this);
	}
}
