package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRIInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBal;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBeq;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBal extends BasicRIInstruction {

	public static final String NAME = "Branch and link";
	public static final String MNEMONIC = "bal";
	public static final int OPERATION_CODE = 0b000001;
	public static final int FUNCTION_CODE = 0b10001;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.SIGNED_16_BIT};

	public InstructionBal() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBal(origin, this, parameters[0].getImmediate());
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBal(instructionCode, this, this);
	}
}
