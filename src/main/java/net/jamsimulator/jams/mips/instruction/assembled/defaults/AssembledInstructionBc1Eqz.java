package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc1eqz;

public class AssembledInstructionBc1Eqz extends AssembledIFPUInstruction {

	public AssembledInstructionBc1Eqz(int targetRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBc1Eqz> basicOrigin) {
		super(InstructionBc1eqz.OPERATION_CODE, InstructionBc1eqz.BASE_CODE, targetRegister, offset, origin, basicOrigin);
	}

	public AssembledInstructionBc1Eqz(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBc1Eqz> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}