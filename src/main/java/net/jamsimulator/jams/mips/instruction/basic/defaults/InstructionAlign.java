package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAlign;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAlign extends BasicRInstruction {

	public static final String NAME = "Concatenate two GRPs extracting a contiguous subset at a byte position";
	public static final String MNEMONIC = "align";
	public static final int OPERATION_CODE = 0b011111;
	public static final int FUNCTION_CODE = 0b100000;
	public static final int ALIGN_CODE = 0b010;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER,
			ParameterType.REGISTER, ParameterType.UNSIGNED_5_BIT};

	public InstructionAlign() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
	}


	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> CompiledInstructionAlign.ALIGN_CODE_SHIFT) & CompiledInstructionAlign.ALIGN_CODE_MASK) == ALIGN_CODE;
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionAlign(parameters[1].getRegister(), parameters[2].getRegister(),
				parameters[0].getRegister(), parameters[3].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAlign(instructionCode, this, this);
	}
}
