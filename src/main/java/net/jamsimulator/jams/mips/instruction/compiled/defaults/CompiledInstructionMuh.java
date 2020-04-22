package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMuh;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRSOPInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionMuh extends CompiledRSOPInstruction {

	public CompiledInstructionMuh(int sourceRegister, int targetRegister, int destinationRegister,
								  Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionMuh.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMuh.SOP_CODE,
				InstructionMuh.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionMuh(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> rs = set.getRegister(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rt = set.getRegister(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		Optional<Register> rd = set.getRegister(getDestinationRegister());
		if (!rd.isPresent()) error("Destination register not found");

		long l = (long) (rs.get().getValue()) * rt.get().getValue();
		rd.get().setValue((int) (l >> 32));
	}
}
