package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledRInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * an instruction code.
 */
public abstract class BasicRInstruction extends BasicInstruction {

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
	public BasicRInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
							 int functionCode) {
		super(name, mnemonic, parameters, operationCode);
		this.functionCode = functionCode;
	}

	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				(instructionCode & CompiledRInstruction.FUNCTION_CODE_MASK) == functionCode;
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
