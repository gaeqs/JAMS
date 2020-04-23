package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddDouble;

public class AssembledInstructionAddDouble extends AssembledRFPUInstruction {

	public AssembledInstructionAddDouble(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionAddDouble> basicOrigin) {
		super(InstructionAddDouble.OPERATION_CODE, InstructionAddDouble.FMT, targetRegister, sourceRegister, destinationRegister,
				InstructionAddDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAddDouble(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAddDouble> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
