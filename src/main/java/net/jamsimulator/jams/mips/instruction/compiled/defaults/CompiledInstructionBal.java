package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionBal;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRIInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionBal extends CompiledRIInstruction {

	public CompiledInstructionBal(Instruction origin, BasicInstruction basicOrigin, int immediate) {
		super(InstructionBal.OPERATION_CODE, 0, InstructionBal.FUNCTION_CODE, immediate, origin, basicOrigin);
	}

	public CompiledInstructionBal(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> ra = set.getRegister(31);
		if (!ra.isPresent()) error("Return address register not found.");

		Register pc = set.getProgramCounter();
		ra.get().setValue(pc.getValue());
		pc.setValue(pc.getValue() + (getImmediateAsSigned() << 2));

	}
}
