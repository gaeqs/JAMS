package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionLw;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionLw extends CompiledI16Instruction {

	public CompiledInstructionLw(int baseRegister, int targetRegister, int offset, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionLw.OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
	}

	public CompiledInstructionLw(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> base = set.getRegister(getSourceRegister());
		if (!base.isPresent()) error("Base register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");

		int address = base.get().getValue() + getImmediateAsSigned();
		int word = simulation.getMemory().getWord(address);
		rt.get().setValue(word);
	}
}
