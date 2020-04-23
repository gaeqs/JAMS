package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;

public class SingleCycleSimulation extends Simulation<SingleCycleArchitecture> {


	public SingleCycleSimulation(SingleCycleArchitecture architecture, InstructionSet instructionSet, Registers registerSet, Memory memory) {
		super(architecture, instructionSet, registerSet, memory);
	}

	@Override
	public void nextStep() {
		int pc = registerSet.getProgramCounter().getValue();

		//Fetch and Decode
		registerSet.getProgramCounter().setValue(pc + 4);
		AssembledInstruction instruction = fetch(pc);

		if (instruction == null) {
			int code = memory.getWord(pc);
			throw new InstructionNotFoundException("Couldn't decode instruction 0x" +
					addZeros(Integer.toHexString(code), 8) + ". (" + addZeros(Integer.toBinaryString(code), 32) + ")");
		}

		String address = "0x" + addZeros(Integer.toHexString(pc), 8);
		String opCode = addZeros(Integer.toBinaryString(instruction.getOperationCode()), 6);
		String mnemonic = instruction.getBasicOrigin().getMnemonic();
		String code = "0x" + addZeros(Integer.toHexString(instruction.getCode()), 8);
		System.out.println(address + "\t" + opCode + "\t" + mnemonic + " \t" + code);

		//Execute, Memory and Write

		SingleCycleExecution<?> execution = (SingleCycleExecution<?>)
				instruction.getBasicOrigin().generateExecution(this, instruction).orElse(null);
		if (execution == null) {
			throw new InstructionNotFoundException("Couldn't decode instruction " + code + ".");
		}
		execution.execute();
	}

	@Override
	public void executeAll() {

	}

	private String addZeros(String s, int to) {
		StringBuilder builder = new StringBuilder();
		int max = Math.max(0, to - s.length());

		for (int i = 0; i < max; i++) {
			builder.append("0");
		}

		return builder + s;
	}
}
