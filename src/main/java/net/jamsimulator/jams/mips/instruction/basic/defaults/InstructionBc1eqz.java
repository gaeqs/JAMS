package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBc1eqz;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBc1eqz extends BasicIFPUInstruction {

	public static final String NAME = "Branch if COP1 register bit 0 equal to zero";
	public static final String MNEMONIC = "bc1eqz";
	public static final int OPERATION_CODE = 0b010001;
	public static final int BASE_CODE = 0b01001;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBc1eqz() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, BASE_CODE);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBc1eqz(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBc1eqz(instructionCode, this, this);
	}
}
