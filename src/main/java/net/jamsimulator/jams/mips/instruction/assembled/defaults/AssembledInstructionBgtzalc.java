package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBgtzalc;

public class AssembledInstructionBgtzalc extends AssembledI16Instruction {

	public AssembledInstructionBgtzalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBgtzalc> basicOrigin) {
		super(InstructionBgtzalc.OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBgtzalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBgtzalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
