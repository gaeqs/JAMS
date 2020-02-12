package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBeq;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBgez;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRIInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBgez extends CompiledRIInstruction {

	public CompiledInstructionBgez(int sourceRegister, int offset, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionBgez.OPERATION_CODE, sourceRegister, InstructionBgez.FUNCTION_CODE, offset, origin, basicOrigin);
	}

	public CompiledInstructionBgez(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");

		if (rs.get().getValue() < 0) return;

		Register pc = set.getProgramCounter();
		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}
