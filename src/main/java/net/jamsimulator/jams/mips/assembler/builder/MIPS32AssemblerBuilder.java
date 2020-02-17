package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.mips.assembler.MIPS32Assembler;
import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.RegisterSet;

public class MIPS32AssemblerBuilder extends AssemblerBuilder {

	public static final String NAME = "MIPS32";


	/**
	 * Creates a MIPS32 builder.
	 */
	public MIPS32AssemblerBuilder() {
		super(NAME);
	}

	@Override
	public MIPS32Assembler createAssembler(DirectiveSet directiveSet, InstructionSet instructionSet, RegisterSet registerSet, Memory memory) {
		return new MIPS32Assembler(directiveSet, instructionSet, registerSet, memory);
	}
}
