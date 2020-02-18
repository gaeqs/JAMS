package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBeqzalc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBeqzalc extends BasicInstruction {

	public static final String NAME = "Branch and link on equal to zero compact";
	public static final String MNEMONIC = "beqzalc";
	public static final int OPERATION_CODE = 0b001000;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBeqzalc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBeqzalc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBeqzalc(instructionCode, this, this);
	}

	@Override
	public boolean match(int instructionCode) {
		int rs = instructionCode >> CompiledI16Instruction.SOURCE_REGISTER_SHIFT & CompiledI16Instruction.SOURCE_REGISTER_SHIFT;
		int rt = instructionCode >> CompiledI16Instruction.TARGET_REGISTER_SHIFT & CompiledI16Instruction.TARGET_REGISTER_MASK;
		return super.match(instructionCode) && rs == 0 && rt != 0;
	}
}