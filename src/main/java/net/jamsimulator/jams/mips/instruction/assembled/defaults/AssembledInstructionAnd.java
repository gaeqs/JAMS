package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAnd;

public class AssembledInstructionAnd extends AssembledRInstruction {

	public AssembledInstructionAnd(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionAnd> basicOrigin) {
		super(InstructionAnd.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionAnd.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAnd(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAnd> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
