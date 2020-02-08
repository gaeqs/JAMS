package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.CompilerData;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

import java.nio.charset.StandardCharsets;

public class DirectiveAsciiz extends Directive {

	public static final String NAME = "asciiz";

	public DirectiveAsciiz() {
		super(NAME);
	}

	@Override
	public int execute(int line, String[] parameters, Compiler compiler) {
		if (parameters.length != 1)
			throw new CompilerException(line, "." + NAME + " must have one string parameter.");

		String s = parameters[0];
		if (!s.startsWith("\"") && !s.endsWith("\""))
			throw new CompilerException(line, "." + NAME + " parameter '" + s + "' is not a string.");

		CompilerData data = compiler.getCompilerData();
		data.align(0);

		int start = data.getCurrent();

		for (byte b : s.getBytes(StandardCharsets.US_ASCII)) {
			compiler.getMemory().setByte(data.getCurrent(), b);
			data.addCurrent(1);
		}
		compiler.getMemory().setByte(data.getCurrent(), (byte) 0);
		data.addCurrent(1);
		return start;
	}
}