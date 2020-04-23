package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionAluipc;
import net.jamsimulator.jams.mips.instruction.basic.BasicPCREL16Instruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class InstructionAluipc extends BasicPCREL16Instruction<AssembledInstructionAluipc> {

	public static final String NAME = "Aligned add upper immediate to PC";
	public static final String MNEMONIC = "aluipc";
	public static final int OPERATION_CODE = 0b111011;
	public static final int PCREL_CODE = 0b11111;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionAluipc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, PCREL_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new AssembledInstructionAluipc(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public AssembledInstruction compileFromCode(int instructionCode) {
		return new AssembledInstructionAluipc(instructionCode, this, this);
	}

	public static class SingleCycle extends SingleCycleExecution<AssembledInstructionAluipc> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, AssembledInstructionAluipc instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Registers set = simulation.getRegisterSet();
			Optional<Register> rs = set.getRegister(instruction.getSourceRegister());
			if (!rs.isPresent()) error("Source register not found.");

			int result = ~0x0FFFF & (set.getProgramCounter().getValue() + (instruction.getImmediate() << 16));
			rs.get().setValue(result);
		}
	}
}
