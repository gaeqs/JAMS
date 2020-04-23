package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionModu;

public class AssembledInstructionModu extends AssembledRSOPInstruction {

	public AssembledInstructionModu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionModu> basicOrigin) {
		super(InstructionModu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionModu.SOP_CODE,
				InstructionModu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionModu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionModu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
