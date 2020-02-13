package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.nio.charset.StandardCharsets;

public class DirectiveAsciiz extends Directive {

	public static final String NAME = "asciiz";

	public DirectiveAsciiz() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have one string parameter.");

		String s = parameters[0];
		if (!s.startsWith("\"") && !s.endsWith("\""))
			throw new AssemblerException(lineNumber, "." + NAME + " parameter '" + s + "' is not a string.");

		AssemblerData data = assembler.getAssemblerData();
		data.align(0);

		int start = data.getCurrent();

		for (byte b : s.getBytes(StandardCharsets.US_ASCII)) {
			assembler.getMemory().setByte(data.getCurrent(), b);
			data.addCurrent(1);
		}
		assembler.getMemory().setByte(data.getCurrent(), (byte) 0);
		data.addCurrent(1);
		return start;
	}
}
