package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBlezalc;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBlezalc extends CompiledI16Instruction {

	public CompiledInstructionBlezalc(int targetRegister, int offset, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionBlezalc.OPERATION_CODE, 0, targetRegister, offset, origin, basicOrigin);
	}

	public CompiledInstructionBlezalc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		Optional<Register> ra = set.getRegister(31);
		if (!ra.isPresent()) error("Return address register not found.");

		if (rt.get().getValue() > 0) return;

		Register pc = set.getProgramCounter();
		ra.get().setValue(pc.getValue());

		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}
