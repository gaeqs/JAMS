package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledPCREL16Instruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a PRCEL-16-Type basic instruction. This subclass adds the pcrel code
 * of the instruction, allowing the simulator to find this instruction based on
 * a pc relative code.
 */
public abstract class BasicPCREL16Instruction<Inst extends AssembledInstruction> extends BasicInstruction<Inst> {

	private final int pcRelFunction;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array and an operation code.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param pcRelFunction the pc relative function code.
	 */
	public BasicPCREL16Instruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
								   int pcRelFunction) {
		super(name, mnemonic, parameters, operationCode);
		this.pcRelFunction = pcRelFunction;
	}

	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> AssembledPCREL16Instruction.PCREL_SHIFT) & AssembledPCREL16Instruction.PCREL_MASK) == pcRelFunction;
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
