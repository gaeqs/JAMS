package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDiv;

public class AssembledInstructionDiv extends AssembledRSOPInstruction {

	public AssembledInstructionDiv(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionDiv> basicOrigin) {
		super(InstructionDiv.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionDiv.SOP_CODE,
				InstructionDiv.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionDiv(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionDiv> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
