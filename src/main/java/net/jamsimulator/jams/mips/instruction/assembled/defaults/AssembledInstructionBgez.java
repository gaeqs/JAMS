package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRIInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBgez;

public class AssembledInstructionBgez extends AssembledRIInstruction {

	public AssembledInstructionBgez(int sourceRegister, int offset, Instruction origin, BasicInstruction<AssembledInstructionBgez> basicOrigin) {
		super(InstructionBgez.OPERATION_CODE, sourceRegister, InstructionBgez.FUNCTION_CODE, offset, origin, basicOrigin);
	}

	public AssembledInstructionBgez(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionBgez> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}
}
