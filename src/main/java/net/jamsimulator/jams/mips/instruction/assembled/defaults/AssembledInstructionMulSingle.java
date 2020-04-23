package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMulSingle;

public class AssembledInstructionMulSingle extends AssembledRFPUInstruction {

	public AssembledInstructionMulSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionMulSingle> basicOrigin) {
		super(InstructionMulSingle.OPERATION_CODE, InstructionMulSingle.FMT, targetRegister,
				sourceRegister, destinationRegister, InstructionMulSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMulSingle(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMulSingle> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}