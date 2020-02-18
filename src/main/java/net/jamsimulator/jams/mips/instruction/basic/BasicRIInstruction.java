package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledRIInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a REGIMM basic instruction. This subclass adds the function code
 * of the instruction, allowing the simulator to find this instruction based on
 * an instruction code.
 */
public abstract class BasicRIInstruction extends BasicInstruction {

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
	public BasicRIInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
							  int functionCode) {
		super(name, mnemonic, parameters, operationCode);
		this.functionCode = functionCode;
	}

	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> CompiledRIInstruction.FUNCTION_CODE_SHIFT)
						& CompiledRIInstruction.FUNCTION_CODE_MASK) == functionCode;
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