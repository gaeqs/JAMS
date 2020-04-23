package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddSingle;

public class AssembledInstructionAddSingle extends AssembledRFPUInstruction {

	public AssembledInstructionAddSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionAddSingle> basicOrigin) {
		super(InstructionAddSingle.OPERATION_CODE, InstructionAddSingle.FMT, targetRegister,
				sourceRegister, destinationRegister, InstructionAddSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAddSingle(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAddSingle> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
