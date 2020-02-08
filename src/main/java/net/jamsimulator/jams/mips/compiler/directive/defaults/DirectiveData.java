package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

public class DirectiveData extends Directive {

	public static final String NAME = "data";

	public DirectiveData() {
		super(NAME);
	}

	@Override
	public int execute(int line, String[] parameters, Compiler compiler) {
		if (parameters.length != 0)
			throw new CompilerException(line, "." + NAME + " directive cannot have parameters.");
		compiler.getCompilerData().setSelected(SelectedMemorySegment.DATA);
		return  -1;
	}
}
