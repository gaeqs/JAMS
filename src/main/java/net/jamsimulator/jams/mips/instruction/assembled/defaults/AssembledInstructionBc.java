package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI26Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc;

public class AssembledInstructionBc extends AssembledI26Instruction {

	public AssembledInstructionBc(Instruction origin, BasicInstruction<AssembledInstructionBc> basicOrigin, int offset) {
		super(InstructionBc.OPERATION_CODE, offset, origin, basicOrigin);
	}

	public AssembledInstructionBc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
