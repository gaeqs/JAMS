package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledRFPUInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;

/**
 * Represents a R-Type FPU basic instruction. This subclass adds the FMT
 * of the instruction, allowing the simulator to find this instruction based on
 * a FMT.
 */
public abstract class BasicRFPUInstruction extends BasicRInstruction {

	private int fmtCode;

	/**
	 * Creates a basic instruction using a name, a mnemonic, a parameter types array, an operation code,
	 * a function code and a operand type format specifier .
	 *
	 * @param name          the name.
	 * @param mnemonic      the mnemonic.
	 * @param parameters    the parameter types.
	 * @param operationCode the operation code.
	 * @param functionCode  the function code.
	 * @param fmtCode       the operand type format specifier.
	 */
	public BasicRFPUInstruction(String name, String mnemonic, ParameterType[] parameters, int operationCode,
								int functionCode, int fmtCode) {
		super(name, mnemonic, parameters, operationCode, functionCode);
		this.fmtCode = fmtCode;
	}

	@Override
	public boolean match(int instructionCode) {
		return super.match(instructionCode) &&
				((instructionCode >> CompiledRFPUInstruction.FMT_SHIFT) & CompiledRFPUInstruction.FMT_MASK) == fmtCode;
	}

	/**
	 * Returns the operand type format specifier of the instruction.
	 *
	 * @return the format specifier.
	 */
	public int getFmtCode() {
		return fmtCode;
	}
}