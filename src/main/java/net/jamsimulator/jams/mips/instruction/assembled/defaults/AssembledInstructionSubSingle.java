package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSubSingle;

public class AssembledInstructionSubSingle extends AssembledRFPUInstruction {

	public AssembledInstructionSubSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionSubSingle> basicOrigin) {
		super(InstructionSubSingle.OPERATION_CODE, InstructionSubSingle.FMT, targetRegister,
				sourceRegister, destinationRegister, InstructionSubSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionSubSingle(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionSubSingle> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
