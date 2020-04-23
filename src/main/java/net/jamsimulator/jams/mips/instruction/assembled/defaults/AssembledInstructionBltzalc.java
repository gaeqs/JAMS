package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBltzalc;

public class AssembledInstructionBltzalc extends AssembledI16Instruction {

	public AssembledInstructionBltzalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBltzalc> basicOrigin) {
		super(InstructionBltzalc.OPERATION_CODE, targetRegister, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBltzalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBltzalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
