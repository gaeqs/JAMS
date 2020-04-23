package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBnezalc;

public class AssembledInstructionBnezalc extends AssembledI16Instruction {

	public AssembledInstructionBnezalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBnezalc> basicOrigin) {
		super(InstructionBnezalc.OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBnezalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBnezalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
