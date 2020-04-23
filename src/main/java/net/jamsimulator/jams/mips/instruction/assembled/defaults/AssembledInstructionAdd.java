package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;

public class AssembledInstructionAdd extends AssembledRInstruction {

	public AssembledInstructionAdd(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionAdd> basicOrigin) {
		super(InstructionAdd.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionAdd.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAdd(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAdd> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
