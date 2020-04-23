package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDivDouble;

public class AssembledInstructionDivDouble extends AssembledRFPUInstruction {

	public AssembledInstructionDivDouble(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionDivDouble> basicOrigin) {
		super(InstructionDivDouble.OPERATION_CODE, InstructionDivDouble.FMT, targetRegister, sourceRegister, destinationRegister,
				InstructionDivDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionDivDouble(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionDivDouble> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

}
