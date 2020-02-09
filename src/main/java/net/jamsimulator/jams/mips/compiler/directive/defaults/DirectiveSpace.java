package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveSpace extends Directive {

	public static final String NAME = "space";

	public DirectiveSpace() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length != 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have one parameter.");

		if (!NumericUtils.isInteger(parameters[0]))
			throw new CompilerException(parameters[0] + " is not a number.");
		int i = Integer.parseInt(parameters[0]);
		if (i < 0) throw new CompilerException(i + " cannot be negative.");

		CompilerData data = compiler.getCompilerData();
		data.align(0);
		int start = data.getCurrent();
		data.addCurrent(i);
		return start;
	}
}
