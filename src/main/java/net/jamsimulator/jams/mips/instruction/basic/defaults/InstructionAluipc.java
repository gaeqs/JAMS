package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicPCREL16Instruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAddiupc;
import net.jamsimulator.jams.mips.instruction.compiled.defaults.CompiledInstructionAluipc;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class InstructionAluipc extends BasicPCREL16Instruction {

	public static final String NAME = "Aligned add upper immediate to PC";
	public static final String MNEMONIC = "aluipc";
	public static final int OPERATION_CODE = 0b111011;
	public static final int PCREL_CODE = 0b11111;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionAluipc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, PCREL_CODE);
	}

	@Override
	public CompiledInstruction compileBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new CompiledInstructionAluipc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public CompiledInstruction compileFromCode(int instructionCode) {
		return new CompiledInstructionAluipc(instructionCode, this, this);
	}
}
