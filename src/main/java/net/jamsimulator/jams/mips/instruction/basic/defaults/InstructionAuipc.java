package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicPCREL16Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAuipc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAuipc extends BasicPCREL16Instruction {

	public static final String NAME = "Add upper immediate to PC";
	public static final String MNEMONIC = "auipc";
	public static final int OPERATION_CODE = 0b111011;
	public static final int PCREL_CODE = 0b11110;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionAuipc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, PCREL_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionAuipc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAuipc(instructionCode, this, this);
	}
}
