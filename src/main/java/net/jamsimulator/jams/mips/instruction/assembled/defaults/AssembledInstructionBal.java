package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRIInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBal;

public class AssembledInstructionBal extends AssembledRIInstruction {

	public AssembledInstructionBal(Instruction origin, BasicInstruction<AssembledInstructionBal> basicOrigin, int offset) {
		super(InstructionBal.OPERATION_CODE, 0, InstructionBal.FUNCTION_CODE, offset, origin, basicOrigin);
	}

	public AssembledInstructionBal(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBal> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
