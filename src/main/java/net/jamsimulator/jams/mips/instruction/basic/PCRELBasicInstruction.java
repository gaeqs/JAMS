package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledPCRELInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * an instruction code.
 */
public abstract class PCRELBasicInstruction extends BasicInstruction {

	private int pcRelFunction;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array and an operation code.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param pcRelFunction the pc relative function code.
	 */
	public PCRELBasicInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
								 int pcRelFunction) {
		super(name, mnemonic, parameters, operationCode);
		this.pcRelFunction = pcRelFunction;
	}

	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> CompiledPCRELInstruction.PCREL_SHIFT) & CompiledPCRELInstruction.PCREL_MASK) == pcRelFunction;
	}

	/**
	 * Returns the pc relative function code of the instruction.
	 *
	 * @return the pc relative function code.
	 */
	public int getPcRelFunction() {
		return pcRelFunction;
	}
}
