package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddu;

public class AssembledInstructionAddu extends AssembledRInstruction {

	public AssembledInstructionAddu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionAddu> basicOrigin) {
		super(InstructionAddu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionAddu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAddu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAddu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
