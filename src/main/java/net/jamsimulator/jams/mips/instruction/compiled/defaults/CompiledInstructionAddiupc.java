package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledPCRELInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAddiupc extends CompiledPCRELInstruction {

	public static final int OPERATION_CODE = 0b111011;
	public static final int PCREL_CODE = 0b00;

	public CompiledInstructionAddiupc(int sourceRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(OPERATION_CODE, sourceRegister, PCREL_CODE, immediate, origin, basicOrigin);
	}

	public CompiledInstructionAddiupc(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		rs.get().setValue(set.getProgramCounter().getValue() + (getImmediateAsSigned() << 2));
	}
}
