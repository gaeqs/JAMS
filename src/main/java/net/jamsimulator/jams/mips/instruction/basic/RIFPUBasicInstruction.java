package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type FPU basic instruction. This subclass adds the subcode
 * of the instruction, allowing the simulator to find this instruction based on
 * a subcode.
 */
public class RIFPUBasicInstruction extends RBasicInstruction {

	private int subcode;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array, an operation code,
	 * a function code and a subcode.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 * @param subcode       the subcode.
	 */
	public RIFPUBasicInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
								 int functionCode, int subcode) {
		super(name, mnemonic, parameters, operationCode, functionCode);
		this.subcode = subcode;
	}

	@Override
	public boolean match(int operationCode, int functionCode, int fmtSub) {
		return super.match(operationCode, functionCode) && fmtSub == this.subcode;
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
