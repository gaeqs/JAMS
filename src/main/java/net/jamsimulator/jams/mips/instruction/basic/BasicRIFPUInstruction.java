package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRIFPUInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a RI-Type FPU basic instruction. This subclass adds the subcode
 * of the instruction, allowing the simulator to find this instruction based on
 * a subcode.
 */
public abstract class BasicRIFPUInstruction<Inst extends AssembledInstruction> extends BasicInstruction<Inst> {

	private final int subcode;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array, an operation code,
	 * and a subcode.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param subcode       the subcode.
	 */
	public BasicRIFPUInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode, int subcode) {
		super(name, mnemonic, parameters, operationCode);
		this.subcode = subcode;
	}


	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> AssembledRIFPUInstruction.SUBCODE_SHIFT) & AssembledRIFPUInstruction.SUBCODE_MASK) == subcode;
	}

	/**
	 * Returns the subcode.
	 *
	 * @return the subcode.
	 */
	public int getSubcode() {
		return subcode;
	}
}
