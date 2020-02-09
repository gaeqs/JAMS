package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class CompiledInstructionAddSingle extends CompiledRFPUInstruction {

	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10000;
	public static final int FUNCTION_CODE = 0b000000;

	public CompiledInstructionAddSingle(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction basicOrigin) {
		super(OPERATION_CODE, FMT, targetRegister, sourceRegister, destinationRegister, FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionAddSingle(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
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

		float f = Float.intBitsToFloat(rt.get().getValue()) + Float.intBitsToFloat(rs.get().getValue());
		rd.get().setValue(Float.floatToIntBits(f));
	}
}
