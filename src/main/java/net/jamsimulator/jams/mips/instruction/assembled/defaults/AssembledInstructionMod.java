package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMod;

public class AssembledInstructionMod extends AssembledRSOPInstruction {

	public AssembledInstructionMod(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction<AssembledInstructionMod> basicOrigin) {
		super(InstructionMod.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMod.SOP_CODE,
				InstructionMod.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMod(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMod> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
