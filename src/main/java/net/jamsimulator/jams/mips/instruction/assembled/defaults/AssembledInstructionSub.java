package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSub;

public class AssembledInstructionSub extends AssembledRInstruction {

	public AssembledInstructionSub(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionSub> basicOrigin) {
		super(InstructionSub.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionSub.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionSub(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionSub> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}