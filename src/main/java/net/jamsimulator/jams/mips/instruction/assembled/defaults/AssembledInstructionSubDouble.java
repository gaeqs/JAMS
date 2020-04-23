package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSubDouble;

public class AssembledInstructionSubDouble extends AssembledRFPUInstruction {

	public AssembledInstructionSubDouble(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionSubDouble> basicOrigin) {
		super(InstructionSubDouble.OPERATION_CODE, InstructionSubDouble.FMT, targetRegister, sourceRegister, destinationRegister,
				InstructionSubDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionSubDouble(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionSubDouble> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}