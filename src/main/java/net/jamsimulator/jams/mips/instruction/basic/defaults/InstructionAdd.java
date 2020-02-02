package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.RBasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAdd;
import net.jamsimulator.jams.mips.parameter.ParameterType;

public class InstructionAdd extends RBasicInstruction {

	public static final String NAME = "Addition";
	public static final String MNEMONIC = "add";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b100000;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionAdd() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(Object[] parameters) {
		return new CompiledInstructionAdd((int) parameters[1], (int) parameters[2],
				(int) parameters[0], this, this);
	}
}
