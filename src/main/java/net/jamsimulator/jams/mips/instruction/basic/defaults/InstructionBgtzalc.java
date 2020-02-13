package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionBgtzalc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionBgtzalc extends BasicInstruction {

	public static final String NAME = "Branch and link on greater than to zero compact";
	public static final String MNEMONIC = "bgtzalc";
	public static final int OPERATION_CODE = 0b000111;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBgtzalc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
	}

	@Override
	public CompiledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionBgtzalc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionBgtzalc(instructionCode, this, this);
	}

	@Override
	public boolean match(int instructionCode) {
		int rs = instructionCode >> CompiledI16Instruction.SOURCE_REGISTER_SHIFT & CompiledI16Instruction.SOURCE_REGISTER_SHIFT;
		int rt = instructionCode >> CompiledI16Instruction.TARGET_REGISTER_SHIFT & CompiledI16Instruction.TARGET_REGISTER_MASK;
		return super.match(instructionCode) && rs == 0 && rt != 0;
	}
}
