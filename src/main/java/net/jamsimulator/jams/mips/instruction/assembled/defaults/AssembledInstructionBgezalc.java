package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBgezalc;

public class AssembledInstructionBgezalc extends AssembledI16Instruction {

	public AssembledInstructionBgezalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBgezalc> basicOrigin) {
		super(InstructionBgezalc.OPERATION_CODE, targetRegister, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBgezalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBgezalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
