package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.LabelUtils;

public class DirectiveGlobl extends Directive {

	public static final String NAME = "globl";

	public DirectiveGlobl() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length < 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!LabelUtils.isLabelLegal(parameter))
				throw new CompilerException("Illegal label " + parameter + ".");
		}

		for (String parameter : parameters) {
			compiler.setAsGlobalLabel(lineNumber, parameter);

		}

		return -1;
	}
}
