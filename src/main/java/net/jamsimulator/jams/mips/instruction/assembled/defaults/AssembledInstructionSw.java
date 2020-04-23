package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSw;

public class AssembledInstructionSw extends AssembledI16Instruction {

	public AssembledInstructionSw(int baseRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionSw> basicOrigin) {
		super(InstructionSw.OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionSw(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionSw> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}