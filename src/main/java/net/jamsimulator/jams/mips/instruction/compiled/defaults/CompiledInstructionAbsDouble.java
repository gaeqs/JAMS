package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Optional;

public class CompiledInstructionAbsDouble extends CompiledRFPUInstruction {

	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000101;

	public CompiledInstructionAbsDouble(int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction basicOrigin) {
		super(OPERATION_CODE, FMT, 0, sourceRegister, destinationRegister, FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionAbsDouble(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		RegisterSet set = simulation.getRegisterSet();
		Optional<Register> rs0 = set.getCoprocessor1Register(getSourceRegister());
		Optional<Register> rs1 = set.getCoprocessor1Register(getSourceRegister() + 1);
		if (!rs0.isPresent()) error("Source register not found.");
		if (!rs1.isPresent()) error("Source register not found.");
		if (rs0.get().getIdentifier() % 2 != 0) error("Source register identifier is not even.");
		Optional<Register> rd0 = set.getCoprocessor1Register(getDestinationRegister());
		Optional<Register> rd1 = set.getCoprocessor1Register(getDestinationRegister() + 1);
		if (!rd0.isPresent()) error("Destination register not found");
		if (!rd1.isPresent()) error("Destination register not found");
		if (rd0.get().getIdentifier() % 2 != 0) error("Destination register identifier is not even.");

		int d0 = rs0.get().getValue();
		int d1 = rs1.get().getValue();
		double abs = Math.abs(NumericUtils.intsToDouble(d0, d1));
		int[] ints = NumericUtils.doubleToInts(abs);
		rs0.get().setValue(ints[0]);
		rs1.get().setValue(ints[1]);
	}
}
