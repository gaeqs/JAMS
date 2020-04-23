package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAndi;

public class AssembledInstructionAndi extends AssembledI16Instruction {

	public AssembledInstructionAndi(int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction<AssembledInstructionAndi> basicOrigin) {
		super(InstructionAndi.OPERATION_CODE, sourceRegister, targetRegister, immediate, origin, basicOrigin);
	}

	public AssembledInstructionAndi(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAndi> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
