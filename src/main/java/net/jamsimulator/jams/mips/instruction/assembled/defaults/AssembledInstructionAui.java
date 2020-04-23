package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAui;

public class AssembledInstructionAui extends AssembledI16Instruction {

	public AssembledInstructionAui(int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAui> basicOrigin) {
		super(InstructionAui.OPERATION_CODE, sourceRegister, targetRegister, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAui(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAui> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
