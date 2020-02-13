package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionDivSingle;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionDivSingle extends CompiledRFPUInstruction {

	public CompiledInstructionDivSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionDivSingle.OPERATION_CODE, InstructionDivSingle.FMT, targetRegister,
				sourceRegister, destinationRegister, InstructionDivSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionDivSingle(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rt = set.getCoprocessor1Register(getTargetRegister());
		if (!rt.isPresent()) error("Target register not found.");
		Optional<Register> rs = set.getCoprocessor1Register(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rd = set.getCoprocessor1Register(getDestinationRegister());
		if (!rd.isPresent()) error("Destination register not found");

		float f = Float.intBitsToFloat(rs.get().getValue()) / Float.intBitsToFloat(rt.get().getValue());
		rd.get().setValue(Float.floatToIntBits(f));
	}
}
