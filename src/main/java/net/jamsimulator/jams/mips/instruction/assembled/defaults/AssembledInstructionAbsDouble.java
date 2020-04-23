package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAbsDouble;

public class AssembledInstructionAbsDouble extends AssembledRFPUInstruction {

	public AssembledInstructionAbsDouble(int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionAbsDouble> basicOrigin) {
		super(InstructionAbsDouble.OPERATION_CODE, InstructionAbsDouble.FMT, 0, sourceRegister,
				destinationRegister, InstructionAbsDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAbsDouble(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAbsDouble> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
