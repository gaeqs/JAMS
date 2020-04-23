package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledPCREL16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAuipc;

public class AssembledInstructionAuipc extends AssembledPCREL16Instruction {

	public AssembledInstructionAuipc(int sourceRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAuipc> basicOrigin) {
		super(InstructionAuipc.OPERATION_CODE, sourceRegister, InstructionAuipc.PCREL_CODE, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAuipc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAuipc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
