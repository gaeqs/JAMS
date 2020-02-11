package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeq;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBeq extends CompiledI16Instruction {

	public CompiledInstructionBeq(int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionBeq.OPERATION_CODE, sourceRegister, targetRegister, immediate, origin, basicOrigin);
	}

	public CompiledInstructionBeq(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");

		if (rs.get().getValue() != rt.get().getValue()) return;
		Register pc = set.getProgramCounter();
		pc.setValue(pc.getValue() + getImmediateAsSigned() << 2);

	}
}
