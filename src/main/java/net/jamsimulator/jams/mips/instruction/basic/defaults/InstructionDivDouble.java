package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionDivDouble;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionDivDouble extends BasicRFPUInstruction {

	public static final String NAME = "Division (double)";
	public static final String MNEMONIC = "div.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000011;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionDivDouble() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionDivDouble(parameters[2].getRegister(), parameters[1].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionDivDouble(instructionCode, this, this);
	}
}