package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddiu;

public class AssembledInstructionAddiu extends AssembledI16Instruction {

	public AssembledInstructionAddiu(int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAddiu> basicOrigin) {
		super(InstructionAddiu.OPERATION_CODE, sourceRegister, targetRegister, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAddiu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAddiu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
