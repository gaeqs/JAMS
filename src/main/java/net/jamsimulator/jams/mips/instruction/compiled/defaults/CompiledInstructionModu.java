package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionModu;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRSOPInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionModu extends CompiledRSOPInstruction {

	public CompiledInstructionModu(int sourceRegister, int targetRegister, int destinationRegister,
								   Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionModu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionModu.SOP_CODE,
				InstructionModu.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionModu(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
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

		if (rt.get().getValue() == 0) {
			//MIP rev 6: If the divisor in GPR rt is zero, the result value is UNPREDICTABLE.
			rd.get().setValue(0);
			return;
		}
		rd.get().setValue(Integer.remainderUnsigned(rs.get().getValue(), rt.get().getValue()));
	}
}
