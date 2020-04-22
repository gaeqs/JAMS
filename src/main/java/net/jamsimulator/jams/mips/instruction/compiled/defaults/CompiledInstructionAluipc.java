package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAluipc;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledPCREL16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAluipc extends CompiledPCREL16Instruction {

	public CompiledInstructionAluipc(int sourceRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAluipc.OPERATION_CODE, sourceRegister, InstructionAluipc.PCREL_CODE, immediate, origin, basicOrigin);
	}

	public CompiledInstructionAluipc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");

		int result = ~0x0FFFF & (set.getProgramCounter().getValue() + (getImmediate() << 16));
		rs.get().setValue(result);
	}
}
