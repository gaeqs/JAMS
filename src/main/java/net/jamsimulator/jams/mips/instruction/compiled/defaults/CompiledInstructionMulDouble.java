package net.jamsimulator.jams.mips.instruction.compiled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMulDouble;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Optional;

public class CompiledInstructionMulDouble extends CompiledRFPUInstruction {

	public CompiledInstructionMulDouble(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction basicOrigin) {
		super(InstructionMulDouble.OPERATION_CODE, InstructionMulDouble.FMT, targetRegister, sourceRegister, destinationRegister,
				InstructionMulDouble.FUNCTION_CODE, origin, basicOrigin);
	}

	public CompiledInstructionMulDouble(int instructionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public void execute(Simulation simulation) {
		Registers set = simulation.getRegisterSet();
		Optional<Register> rt0 = set.getCoprocessor1Register(getTargetRegister());
		Optional<Register> rt1 = set.getCoprocessor1Register(getTargetRegister() + 1);
		if (!rt0.isPresent()) error("Target register not found.");
		if (!rt1.isPresent()) error("Target register not found.");
		if (rt0.get().getIdentifier() % 2 != 0) error("Target register identifier is not even.");
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

		double target = NumericUtils.intsToDouble(rt0.get().getValue(), rt1.get().getValue());
		double source = NumericUtils.intsToDouble(rs0.get().getValue(), rs1.get().getValue());
		double destination = source * target;
		int[] ints = NumericUtils.doubleToInts(destination);
		rs0.get().setValue(ints[0]);
		rs1.get().setValue(ints[1]);
	}
}
