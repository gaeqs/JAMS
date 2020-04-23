package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBlezalc;

public class AssembledInstructionBlezalc extends AssembledI16Instruction {

	public AssembledInstructionBlezalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBlezalc> basicOrigin) {
		super(InstructionBlezalc.OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBlezalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBlezalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
