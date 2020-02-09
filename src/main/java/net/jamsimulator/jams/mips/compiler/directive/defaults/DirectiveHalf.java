package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveHalf extends Directive {

	public static final String NAME = "half";

	public DirectiveHalf() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length < 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!NumericUtils.isShort(parameter))
				throw new CompilerException(lineNumber, "." + NAME + " parameter '" + parameter + "' is not a half.");
		}

		CompilerData data = compiler.getCompilerData();
		data.align(1);
		int start = data.getCurrent();
		for (String parameter : parameters) {
			compiler.getMemory().setWord(data.getCurrent(), Short.toUnsignedInt(Short.parseShort(parameter)));
			data.addCurrent(2);
		}
		return start;
	}

}
