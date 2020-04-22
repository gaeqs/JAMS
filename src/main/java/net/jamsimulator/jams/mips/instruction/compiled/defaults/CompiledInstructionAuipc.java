package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAuipc;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledPCREL16Instruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAuipc extends CompiledPCREL16Instruction {

	public CompiledInstructionAuipc(int sourceRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAuipc.OPERATION_CODE, sourceRegister, InstructionAuipc.PCREL_CODE, immediate, origin, basicOrigin);
	}

	public CompiledInstructionAuipc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");

		int result = set.getProgramCounter().getValue() + (getImmediate() << 16);
		rs.get().setValue(result);
	}
}
