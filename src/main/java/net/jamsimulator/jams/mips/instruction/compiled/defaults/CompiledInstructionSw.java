package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSw;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionSw extends CompiledI16Instruction {

	public CompiledInstructionSw(int baseRegister, int targetRegister, int offset, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionSw.OPERATION_CODE, baseRegister, targetRegister, offset, origin, basicOrigin);
	}

	public CompiledInstructionSw(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> base = set.getRegister(getSourceRegister());
		if (!base.isPresent()) error("Base register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");

		int address = base.get().getValue() + getImmediateAsSigned();
		simulation.getMemory().setWord(address, rt.get().getValue());
	}
}
