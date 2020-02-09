package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

public class DirectiveKData extends Directive {

	public static final String NAME = "kdata";

	public DirectiveKData() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length != 0)
			throw new CompilerException(lineNumber, "." + NAME + " directive cannot have parameters.");
		compiler.getCompilerData().setSelected(SelectedMemorySegment.KERNEL_DATA);
		return  -1;
	}
}
