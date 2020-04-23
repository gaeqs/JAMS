package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMul;

public class AssembledInstructionMul extends AssembledRSOPInstruction {

	public AssembledInstructionMul(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionMul> basicOrigin) {
		super(InstructionMul.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMul.SOP_CODE,
				InstructionMul.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMul(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMul> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}