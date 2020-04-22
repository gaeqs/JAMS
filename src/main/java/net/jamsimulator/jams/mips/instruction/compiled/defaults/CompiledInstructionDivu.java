package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDivu;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRSOPInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionDivu extends CompiledRSOPInstruction {

	public CompiledInstructionDivu(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionDivu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionDivu.SOP_CODE,
				InstructionDivu.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionDivu(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
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

		if (rt.get().getValue() == 0) {
			//MIP rev 6: If the divisor in GPR rt is zero, the result value is UNPREDICTABLE.
			rd.get().setValue(0);
			return;
		}
		rd.get().setValue(Integer.divideUnsigned(rs.get().getValue(), rt.get().getValue()));
	}
}
