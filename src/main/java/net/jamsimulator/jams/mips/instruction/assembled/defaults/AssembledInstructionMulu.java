package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMulu;

public class AssembledInstructionMulu extends AssembledRSOPInstruction {

	public AssembledInstructionMulu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionMulu> basicOrigin) {
		super(InstructionMulu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMulu.SOP_CODE,
				InstructionMulu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMulu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMulu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}