package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledPCREL16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAluipc;

public class AssembledInstructionAluipc extends AssembledPCREL16Instruction {

	public AssembledInstructionAluipc(int sourceRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAluipc> basicOrigin) {
		super(InstructionAluipc.OPERATION_CODE, sourceRegister, InstructionAluipc.PCREL_CODE, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAluipc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAluipc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
