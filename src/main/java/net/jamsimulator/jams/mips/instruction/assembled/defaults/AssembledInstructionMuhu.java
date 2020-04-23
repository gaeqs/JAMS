package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMuhu;

public class AssembledInstructionMuhu extends AssembledRSOPInstruction {

	public AssembledInstructionMuhu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionMuhu> basicOrigin) {
		super(InstructionMuhu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMuhu.SOP_CODE,
				InstructionMuhu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMuhu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMuhu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}