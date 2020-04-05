package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

public class DirectiveEqv extends Directive {

	public static final String NAME = "eqv";

	public DirectiveEqv() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 2)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least two parameter.");

		String replace = line.substring(("." + NAME + " " + parameters[0]).length()).trim();

		assembler.getCurrentAssemblingFile().getEquivalents().put(parameters[0], replace);

		return -1;
	}

	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {

	}
}
