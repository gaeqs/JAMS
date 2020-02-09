package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveExtern extends Directive {

	public static final String NAME = "extern";

	public DirectiveExtern() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length != 2)
			throw new CompilerException(lineNumber, "." + NAME + " must have two parameter.");

		if (!LabelUtils.isLabelLegal(parameters[0]))
			throw new CompilerException("Label " + parameters[0] + " is not legal.");
		if (!NumericUtils.isInteger(parameters[1]))
			throw new CompilerException(parameters[1] + " is not a number.");
		int i = Integer.parseInt(parameters[1]);
		if (i < 0)
			throw new CompilerException(i + " cannot be negative.");

		CompilerData data = compiler.getCompilerData();
		SelectedMemorySegment old = data.getSelected();
		data.setSelected(SelectedMemorySegment.EXTERN);
		data.align(0);
		int start = data.getCurrent();
		data.addCurrent(i);
		compiler.setAsGlobalLabel(lineNumber, parameters[0]);
		data.setSelected(old);
		return start;
	}
}
