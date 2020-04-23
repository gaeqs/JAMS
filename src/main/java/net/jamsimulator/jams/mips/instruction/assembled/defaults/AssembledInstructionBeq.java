package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeq;

public class AssembledInstructionBeq extends AssembledI16Instruction {

	public AssembledInstructionBeq(int sourceRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBeq> basicOrigin) {
		super(InstructionBeq.OPERATION_CODE, sourceRegister, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBeq(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBeq> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
