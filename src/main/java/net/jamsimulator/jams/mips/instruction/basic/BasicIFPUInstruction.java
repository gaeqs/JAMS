package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.assembled.AssembledIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a I-Type FPU basic instruction. This subclass adds the subcode
 * of the instruction, allowing the simulator to find this instruction based on
 * a subcode.
 */
public abstract class BasicIFPUInstruction<Inst extends AssembledInstruction> extends BasicInstruction<Inst> {

	private int subcode;

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
	public BasicIFPUInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode, int subcode) {
		super(name, mnemonic, parameters, operationCode);
		this.subcode = subcode;
	}


	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> AssembledIFPUInstruction.BASE_REGISTER_SHIFT) & AssembledIFPUInstruction.BASE_REGISTER_MASK) == subcode;
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
