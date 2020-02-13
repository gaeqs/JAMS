package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionSubSingle;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionSubSingle extends BasicRFPUInstruction {

	public static final String NAME = "Subtraction (single)";
	public static final String MNEMONIC = "sub.s";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10000;
	public static final int FUNCTION_CODE = 0b000001;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.FLOAT_REGISTER, ParameterType.FLOAT_REGISTER};

	public InstructionSubSingle() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionSubSingle(parameters[2].getRegister(), parameters[1].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionSubSingle(instructionCode, this, this);
	}
}
