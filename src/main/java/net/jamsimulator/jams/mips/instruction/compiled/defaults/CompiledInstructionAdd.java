package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAdd extends CompiledRInstruction {

	public CompiledInstructionAdd(int sourceRegister, int targetRegister, int destinationRegister,
								  Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAdd.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, 0,
				InstructionAdd.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionAdd(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
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

		try {
			rd.get().setValue(Math.addExact(rs.get().getValue(), rt.get().getValue()));
		} catch (ArithmeticException ex) {
			error("Integer overflow.", ex);
		}
	}
}
