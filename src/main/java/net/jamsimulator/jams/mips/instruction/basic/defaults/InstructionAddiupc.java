package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.basic.PCRELBasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAddiu;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAddiupc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAddiupc extends PCRELBasicInstruction {

	public static final String NAME = "Immediate addition to pc without overflow";
	public static final String MNEMONIC = "addiupc";
	public static final int OPERATION_CODE = 0b111011;
	public static final int PCREL = 0b00;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_32_BIT};

	public InstructionAddiupc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, PCREL);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters) {
		return new CompiledInstructionAddiupc(parameters[0].getRegister(), parameters[1].getImmediate(), this, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAddiupc(instructionCode, this, this);
	}
}
