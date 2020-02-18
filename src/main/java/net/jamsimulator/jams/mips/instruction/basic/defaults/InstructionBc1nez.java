package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBc1eqz;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBc1nez;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBc1nez extends BasicIFPUInstruction {

	public static final String NAME = "Branch if COP1 register bit 0 not equal to zero";
	public static final String MNEMONIC = "bc1nez";
	public static final int OPERATION_CODE = 0b010001;
	public static final int BASE_CODE = 0b01101;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBc1nez() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, BASE_CODE);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBc1nez(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBc1nez(instructionCode, this, this);
	}
}