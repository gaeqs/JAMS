package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBlezalc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBlezalc extends BasicInstruction {

	public static final String NAME = "Branch and link on less than or equal to zero compact";
	public static final String MNEMONIC = "blezalc";
	public static final int OPERATION_CODE = 0b000110;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBlezalc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBlezalc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBlezalc(instructionCode, this, this);
	}

	@Override
	public boolean match(int instructionCode) {
		int rs = instructionCode >> CompiledI16Instruction.SOURCE_REGISTER_SHIFT & CompiledI16Instruction.SOURCE_REGISTER_SHIFT;
		int rt = instructionCode >> CompiledI16Instruction.TARGET_REGISTER_SHIFT & CompiledI16Instruction.TARGET_REGISTER_MASK;
		return super.match(instructionCode) && rs == 0 && rt != 0;
	}
}
