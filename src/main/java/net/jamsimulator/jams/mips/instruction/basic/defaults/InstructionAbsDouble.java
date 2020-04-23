package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionAbsDouble;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Optional;

public class InstructionAbsDouble extends BasicRFPUInstruction<AssembledInstructionAbsDouble> {

	public static final String NAME = "Absolute (double)";
	public static final String MNEMONIC = "abs.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000101;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionAbsDouble() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new AssembledInstructionAbsDouble(parameters[1].getRegister(), parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction compileFromCode(int instructionCode) {
		return new AssembledInstructionAbsDouble(instructionCode, this, this);
	}


	public static class SingleCycle extends SingleCycleExecution<AssembledInstructionAbsDouble> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, AssembledInstructionAbsDouble instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Registers set = simulation.getRegisterSet();
			Optional<Register> rs0 = set.getCoprocessor1Register(instruction.getSourceRegister());
			Optional<Register> rs1 = set.getCoprocessor1Register(instruction.getSourceRegister() + 1);
			if (!rs0.isPresent()) error("Source register not found.");
			if (!rs1.isPresent()) error("Source register not found.");
			if (rs0.get().getIdentifier() % 2 != 0) error("Source register identifier is not even.");
			Optional<Register> rd0 = set.getCoprocessor1Register(instruction.getDestinationRegister());
			Optional<Register> rd1 = set.getCoprocessor1Register(instruction.getDestinationRegister() + 1);
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
}
