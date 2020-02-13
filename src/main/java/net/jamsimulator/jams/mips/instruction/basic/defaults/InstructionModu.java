package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionModu;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionModu extends BasicRSOPInstruction {

	public static final String NAME = "Module unsigned";
	public static final String MNEMONIC = "modu";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b011011;
	public static final int SOP_CODE = 0b00011;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionModu() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, SOP_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionModu(parameters[1].getRegister(),
				parameters[2].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionModu(instructionCode, this, this);
	}
}
