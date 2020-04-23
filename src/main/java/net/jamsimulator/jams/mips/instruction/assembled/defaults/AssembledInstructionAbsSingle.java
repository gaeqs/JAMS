package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAbsSingle;

public class AssembledInstructionAbsSingle extends AssembledRFPUInstruction {

	public AssembledInstructionAbsSingle(int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<AssembledInstructionAbsSingle> basicOrigin) {
		super(InstructionAbsSingle.OPERATION_CODE, InstructionAbsSingle.FMT, 0, sourceRegister,
				destinationRegister, InstructionAbsSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAbsSingle(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAbsSingle> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

}
