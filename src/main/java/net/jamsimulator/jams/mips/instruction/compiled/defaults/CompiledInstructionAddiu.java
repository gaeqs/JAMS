package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAddiu;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledI16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAddiu extends CompiledI16Instruction {

	public CompiledInstructionAddiu(int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAddiu.OPERATION_CODE, sourceRegister, targetRegister, immediate, origin, basicOrigin);
	}

	public CompiledInstructionAddiu(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		rt.get().setValue(rs.get().getValue() + getImmediateAsSigned());
	}
}
