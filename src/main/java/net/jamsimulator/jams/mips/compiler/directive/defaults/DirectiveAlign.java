package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveAlign extends Directive {

	public static final String NAME = "align";

	public DirectiveAlign() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length != 1 || !NumericUtils.isInteger(parameters[0]))
			throw new CompilerException(lineNumber, "." + NAME + " must have a numeric parameter.");
		int exp = Integer.parseInt(parameters[0]);
		if (exp < 0 || exp > 3)
			throw new CompilerException(lineNumber, "." + NAME + " parameter must be inside the range [0, 3].");
		compiler.getCompilerData().setNextForcedAlignment(exp);
		return -1;
	}
}
