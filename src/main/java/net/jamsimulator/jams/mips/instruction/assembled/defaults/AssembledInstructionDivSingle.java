package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDivSingle;

public class AssembledInstructionDivSingle extends AssembledRFPUInstruction {

	public AssembledInstructionDivSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionDivSingle> basicOrigin) {
		super(InstructionDivSingle.OPERATION_CODE, InstructionDivSingle.FMT, targetRegister,
				sourceRegister, destinationRegister, InstructionDivSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionDivSingle(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionDivSingle> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
