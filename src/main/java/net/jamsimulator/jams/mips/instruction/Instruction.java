package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.List;

/**
 * Represents a MIPS instruction. This instruction is used in compile time to compile a program.
 * Plugins may add more instructions to the simulator.
 * <p>
 * Instruction implementations must be immutable classes. The data of an instruction cannot change.
 * <p>
 * Several instructions with the same mnemonic but different parameters may coexists at the same time.
 * For example, these two instructions are different {@link Instruction} instances:
 * <b>
 * <p>
 * add $t0,$t1,$t2
 * <p>
 * add $t0,$t1,100
 * <p>
 * </b>
 * The first one is a basic instruction that has three registers as parameters, and the second one
 * is a pseudo instruction that has two registers and a 16-bit immediate as parameters.
 * <p>
 * Two instructions with the same mnemonic and parameter types cannot coexist in the simulator.
 * <p>
 * Base instructions and pseudo instructions are divided into two different classes,
 * BasicInstruction and PseudoInstruction.
 */
public interface Instruction {

	/**
	 * Returns the name of the instruction. This is not the MIPS mnemonic, but an human-like name.
	 * For example, the name for the "add" instruction may be "Addition".
	 * <p>
	 * This name depends on the current language of JAMS.
	 * <p>
	 * For the mnemonic of the instruction see {@link #getMnemonic()}.
	 *
	 * @return the human-like name.
	 */
	String getName();

	/**
	 * Returns the MIPS mnemonic of the instruction. This is the static short MIPS name of the instruction,
	 * and it's used to filter and check the instruction in compile time.
	 * <p>
	 * For an human-like name see {@link #getName()}.
	 *
	 * @return the MIPS mnemonic.
	 */
	String getMnemonic();

	/**
	 * Returns the parameter types of the instruction. These {@link ParameterType}s are used
	 * to filter several instructions with the same mnemonic in compile time.
	 * <p>
	 * The returned array is a copy of the original one, so you cannot change the parameters of an instruction.
	 *
	 * @return the parameter types of the instruction.
	 */
	ParameterType[] getParameters();

	/**
	 * Returns whether the instruction matches the given mnemonic and parameter types.
	 *
	 * @param mnemonic   the given mnemonic.
	 * @param parameters the given parameter types.
	 * @return whether the instruction matches the given mnemonic and parameter types.
	 */
	boolean match(String mnemonic, ParameterType[] parameters);

	/**
	 * Returns whether the instruction matches the given mnemonic and a combination of the given parameter types.
	 *
	 * @param mnemonic   the given mnemonic.
	 * @param parameters the given possible parameter types.
	 * @return whether the instruction matches the given mnemonic and parameter types.
	 */
	boolean match(String mnemonic, List<ParameterType>[] parameters);

	/**
	 * Compiles the instruction using the given parameters.
	 *
	 * @param parameters the parameters.
	 * @return a {@link CompiledInstruction} array.
	 */
	CompiledInstruction[] compile(ParameterParseResult[] parameters);

}
