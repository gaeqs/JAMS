package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAlign;

public class AssembledInstructionAlign extends AssembledRInstruction {

	public static final int ALIGN_CODE_SHIFT = 8;
	public static final int ALIGN_CODE_MASK = 0x7;
	public static final int SHIFT_AMOUNT_MASK = 0x3;

	public AssembledInstructionAlign(int sourceRegister, int targetRegister, int destinationRegister, int shiftAmount,
									 Instruction origin, BasicInstruction<AssembledInstructionAlign> basicOrigin) {
		super(InstructionAlign.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister,
				(InstructionAlign.ALIGN_CODE << (ALIGN_CODE_SHIFT - SHIFT_AMOUNT_SHIFT)) + shiftAmount,
				InstructionAlign.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAlign(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAlign> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}


	@Override
	public int getShiftAmount() {
		return super.getShiftAmount() & SHIFT_AMOUNT_MASK;
	}

	public int getAlignCode() {
		return value >> ALIGN_CODE_SHIFT & ALIGN_CODE_MASK;
	}
}
