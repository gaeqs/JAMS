package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSub;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructioSub extends CompiledRInstruction {

	public CompiledInstructioSub(int sourceRegister, int targetRegister, int destinationRegister,
								 Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionSub.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionSub.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructioSub(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		Optional<Register> rd = set.getRegister(getDestinationRegister());
		if (!rd.isPresent()) error("Destination register not found");

		try {
			rd.get().setValue(Math.subtractExact(rs.get().getValue(), rt.get().getValue()));
		} catch (ArithmeticException ex) {
			error("Integer overflow.", ex);
		}
	}
}
