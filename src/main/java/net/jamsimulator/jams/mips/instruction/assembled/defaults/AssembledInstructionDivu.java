package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDivu;

public class AssembledInstructionDivu extends AssembledRSOPInstruction {

	public AssembledInstructionDivu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionDivu> basicOrigin) {
		super(InstructionDivu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionDivu.SOP_CODE,
				InstructionDivu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionDivu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionDivu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
