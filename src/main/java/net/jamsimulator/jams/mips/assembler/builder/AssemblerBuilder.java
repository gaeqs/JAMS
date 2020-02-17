package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public abstract class AssemblerBuilder {

	private String name;

	public AssemblerBuilder(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract Assembler createAssembler(DirectiveSet directiveSet, InstructionSet instructionSet, RegisterSet registerSet, Memory memory);

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
