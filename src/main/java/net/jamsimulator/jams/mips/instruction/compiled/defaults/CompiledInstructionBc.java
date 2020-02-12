package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBc;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI26Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class CompiledInstructionBc extends CompiledI26Instruction {

	public CompiledInstructionBc(Instruction origin, BasicInstruction basicOrigin, int offset) {
		super(InstructionBc.OPERATION_CODE, offset, origin, basicOrigin);
	}

	public CompiledInstructionBc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();

		Register pc = set.getProgramCounter();
		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}
