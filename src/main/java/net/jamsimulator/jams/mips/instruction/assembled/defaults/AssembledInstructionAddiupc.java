package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledPCREL19Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddiupc;

public class AssembledInstructionAddiupc extends AssembledPCREL19Instruction {

	public AssembledInstructionAddiupc(int sourceRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAddiupc> basicOrigin) {
		super(InstructionAddiupc.OPERATION_CODE, sourceRegister, InstructionAddiupc.PCREL_CODE, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAddiupc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAddiupc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
