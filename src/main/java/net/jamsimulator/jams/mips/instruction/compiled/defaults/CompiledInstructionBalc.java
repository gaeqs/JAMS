package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBalc;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI26Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBalc extends CompiledI26Instruction {

	public CompiledInstructionBalc(Instruction origin, BasicInstruction basicOrigin, int offset) {
		super(InstructionBalc.OPERATION_CODE, offset, origin, basicOrigin);
	}

	public CompiledInstructionBalc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> ra = set.getRegister(31);
		if (!ra.isPresent()) error("Return address register not found.");

		Register pc = set.getProgramCounter();
		ra.get().setValue(pc.getValue());
		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}
