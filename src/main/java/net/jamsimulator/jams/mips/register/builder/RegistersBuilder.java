package net.jamsimulator.jams.mips.register.builder;

import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a registers builder. Registers builders are used to create several {@link Registers}
 * using the given parameters.
 * <p>
 * If a plugin wants to add a custom {@link Registers} to JAMS, it should create a child of this class and register
 * it on the manager.
 */
public abstract class RegistersBuilder {

	protected final String name;

	protected final Set<String> registersNames;
	protected final Set<String> generalRegistersNames;
	protected final Set<String> coprocessor0RegistersNames;
	protected final Set<String> coprocessor1RegistersNames;


	protected final Set<Character> validRegistersStarts;

	/**
	 * Creates the builder.
	 *
	 * @param name                 the name of the builder. This name must be unique.
	 * @param registersNames       the registers that will be created by this builder. These names don't contain any start character.
	 * @param validRegistersStarts the valid starts for registers.
	 */
	public RegistersBuilder(String name, Set<String> registersNames, Set<String> generalRegistersNames,
							Set<String> coprocessor0RegistersNames, Set<String> coprocessor1RegistersNames,
							Set<Character> validRegistersStarts) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(registersNames, "Names cannot be null!");
		Validate.notNull(validRegistersStarts, "Valid registers starts cannot be null!");
		this.name = name;
		this.registersNames = registersNames;
		this.generalRegistersNames = generalRegistersNames;
		this.coprocessor0RegistersNames = coprocessor0RegistersNames;
		this.coprocessor1RegistersNames = coprocessor1RegistersNames;
		this.validRegistersStarts = validRegistersStarts;
	}

	public String getName() {
		return name;
	}

	public Set<Character> getValidRegistersStarts() {
		return Collections.unmodifiableSet(validRegistersStarts);
	}

	public Set<String> getRegistersNames() {
		return Collections.unmodifiableSet(registersNames);
	}

	public Set<String> getGeneralRegistersNames() {
		return Collections.unmodifiableSet(generalRegistersNames);
	}

	public Set<String> getCoprocessor0RegistersNames() {
		return Collections.unmodifiableSet(coprocessor0RegistersNames);
	}

	public Set<String> getCoprocessor1RegistersNames() {
		return Collections.unmodifiableSet(coprocessor1RegistersNames);
	}

	public boolean containsRegister(String name) {
		return registersNames.contains(name);
	}

	public abstract Registers createRegisters();
}
