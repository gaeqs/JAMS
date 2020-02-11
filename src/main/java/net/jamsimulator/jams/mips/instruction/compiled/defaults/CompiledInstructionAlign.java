package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAlign;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAlign extends CompiledRInstruction {

	public static final int ALIGN_CODE_SHIFT = 8;
	public static final int ALIGN_CODE_MASK = 0x7;
	public static final int SHIFT_AMOUNT_MASK = 0x3;

	public CompiledInstructionAlign(int sourceRegister, int targetRegister, int destinationRegister, int shiftAmount,
									Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAlign.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister,
				(InstructionAlign.ALIGN_CODE << (ALIGN_CODE_SHIFT - SHIFT_AMOUNT_SHIFT)) + shiftAmount,
				InstructionAlign.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionAlign(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}


	@Override
	public int getShiftAmount() {
		return super.getShiftAmount() & SHIFT_AMOUNT_MASK;
	}

	public int getAlignCode() {
		return value >> ALIGN_CODE_SHIFT & ALIGN_CODE_MASK;
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		Optional<Register> rd = set.getRegister(getDestinationRegister());
		if (!rd.isPresent()) error("Destination register not found");

		int bp = getShiftAmount();
		int tmpRtHi = rt.get().getValue() << (bp << 3);
		int tmpRsLo = rs.get().getValue() >>> ((4 - bp) << 3);
		rd.get().setValue(tmpRtHi | tmpRsLo);
	}
}
