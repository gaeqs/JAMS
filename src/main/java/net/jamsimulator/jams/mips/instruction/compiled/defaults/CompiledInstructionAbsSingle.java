package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAbsSingle;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAbsSingle extends CompiledRFPUInstruction {

	public CompiledInstructionAbsSingle(int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionAbsSingle.OPERATION_CODE, InstructionAbsSingle.FMT, 0, sourceRegister,
				destinationRegister, InstructionAbsSingle.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionAbsSingle(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs = set.getCoprocessor1Register(getSourceRegister());
		if (!rs.isPresent()) error("Source register not found.");
		Optional<Register> rd = set.getCoprocessor1Register(getDestinationRegister());
		if (!rd.isPresent()) error("Destination register not found");

		float f = Math.abs(Float.intBitsToFloat(rs.get().getValue()));
		rd.get().setValue(Float.floatToIntBits(f));
	}
}
