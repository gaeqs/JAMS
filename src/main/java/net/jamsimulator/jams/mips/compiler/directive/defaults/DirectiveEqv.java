package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

public class DirectiveEqv extends Directive {

	public static final String NAME = "eqv";

	public DirectiveEqv() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length < 2)
			throw new CompilerException(lineNumber, "." + NAME + " must have at least two parameter.");

		String replace = line.substring(("." + NAME + " " + parameters[0]).length()).trim();

		compiler.getCurrentCompilingFile().getEquivalents().put(parameters[0], replace);

		return -1;
	}
}
