package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveFloat extends Directive {

	public static final String NAME = "float";

	public DirectiveFloat() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length < 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isFloat(parameter))
				throw new CompilerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a float.");
		}

		CompilerData data = compiler.getCompilerData();
		data.align(2);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			compiler.getMemory().setWord(data.getCurrent(), Float.floatToIntBits(Float.parseFloat(parameter)));
			data.addCurrent(4);
		}
		return start;
	}
}
