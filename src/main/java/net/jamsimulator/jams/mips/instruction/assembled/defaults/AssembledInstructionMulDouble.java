package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMulDouble;

public class AssembledInstructionMulDouble extends AssembledRFPUInstruction {

	public AssembledInstructionMulDouble(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionMulDouble> basicOrigin) {
		super(InstructionMulDouble.OPERATION_CODE, InstructionMulDouble.FMT, targetRegister, sourceRegister, destinationRegister,
				InstructionMulDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMulDouble(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMulDouble> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}