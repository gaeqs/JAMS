package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI26Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBalc;

public class AssembledInstructionBalc extends AssembledI26Instruction {

	public AssembledInstructionBalc(Instruction origin, BasicInstruction<AssembledInstructionBalc> basicOrigin, int offset) {
		super(InstructionBalc.OPERATION_CODE, offset, origin, basicOrigin);
	}

	public AssembledInstructionBalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
