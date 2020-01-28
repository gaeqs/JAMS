package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * a instruction code.
 */
public class RBasicInstruction extends BasicInstruction {

	private int functionCode;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array and an operation code.
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 */
	public RBasicInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
							 int functionCode) {
		super(name, mnemonic, parameters, operationCode);
		this.functionCode = functionCode;
	}

	@Override
	public boolean match(int operationCode, int functionCode) {
		return super.match(operationCode, functionCode) && functionCode == this.functionCode;
	}

	/**
	 * Returns the function code of the instruction.
	 *
	 * @return the function code.
	 */
	public int getFunctionCode() {
		return functionCode;
	}
}
