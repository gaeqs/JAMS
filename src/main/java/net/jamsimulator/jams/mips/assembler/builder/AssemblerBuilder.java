package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents an assembler builder. Assembler builders are used to create several {@link Assembler}
 * using the given parameters.
 * <p>
 * If a plugin want to add a custom assembler to JAMS, it should create a child of this class and register
 * it on the {@link net.jamsimulator.jams.manager.AssemblerBuilderManager}.
 */
public abstract class AssemblerBuilder {

	private String name;

	/**
	 * Creates an assembler builder using a name.
	 * This name must be unique for each assembler builder.
	 *
	 * @param name the name.
	 */
	public AssemblerBuilder(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	/**
	 * Returns the name of this assembler builder.
	 * This name must be unique for each assembler builder.
	 *
	 * @return the name of this assembler builder.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates an {@link Assembler} using a {@link DirectiveSet}, an {@link InstructionSet}, a {@link Registers}
	 * and a {@link Memory}.
	 *
	 * @param directiveSet   the directive set.
	 * @param instructionSet the instruction set.
	 * @param registerSet    the register set.
	 * @param memory         the memory.
	 * @return the new {@link Assembler}.
	 */
	public abstract Assembler createAssembler(DirectiveSet directiveSet, InstructionSet instructionSet, Registers registerSet, Memory memory);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AssemblerBuilder that = (AssemblerBuilder) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
