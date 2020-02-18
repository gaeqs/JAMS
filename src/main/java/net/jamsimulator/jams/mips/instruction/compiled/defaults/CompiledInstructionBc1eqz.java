package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc1eqz;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledIFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBc1eqz extends CompiledIFPUInstruction {

	public CompiledInstructionBc1eqz(int targetRegister, int offset, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionBc1eqz.OPERATION_CODE, InstructionBc1eqz.BASE_CODE, targetRegister, offset, origin, basicOrigin);
	}

	public CompiledInstructionBc1eqz(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rt = set.getCoprocessor1Register(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");

		if((rt.get().getValue() & 1) != 0) return;

		Register pc = set.getProgramCounter();
		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}