package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeqzalc;

public class AssembledInstructionBeqzalc extends AssembledI16Instruction {

	public AssembledInstructionBeqzalc(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBeqzalc> basicOrigin) {
		super(InstructionBeqzalc.OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBeqzalc(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBeqzalc> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
