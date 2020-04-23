package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc1nez;

public class AssembledInstructionBc1Nez extends AssembledIFPUInstruction {

	public AssembledInstructionBc1Nez(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBc1Nez> basicOrigin) {
		super(InstructionBc1nez.OPERATION_CODE, InstructionBc1nez.BASE_CODE, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBc1Nez(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBc1Nez> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
