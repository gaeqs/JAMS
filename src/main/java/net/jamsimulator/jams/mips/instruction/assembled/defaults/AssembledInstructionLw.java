package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionLw;

public class AssembledInstructionLw extends AssembledI16Instruction {

	public AssembledInstructionLw(int baseRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionLw> basicOrigin) {
		super(InstructionLw.OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionLw(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionLw> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
