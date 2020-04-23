package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMuh;

public class AssembledInstructionMuh extends AssembledRSOPInstruction {

	public AssembledInstructionMuh(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionMuh> basicOrigin) {
		super(InstructionMuh.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMuh.SOP_CODE,
				InstructionMuh.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMuh(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMuh> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
