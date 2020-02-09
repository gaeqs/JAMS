package net.jamsimulator.jams.mips.compiler.directive.defaults;

import net.jamsimulator.jams.mips.compiler.Compiler;
import net.jamsimulator.jams.mips.compiler.directive.Directive;
import net.jamsimulator.jams.mips.compiler.exception.CompilerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DirectiveInclude extends Directive {

	public static final String NAME = "include";

	public DirectiveInclude() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Compiler compiler) {
		if (parameters.length != 1)
			throw new CompilerException(lineNumber, "." + NAME + " must have one parameter.");


		File file = new File(parameters[0]);
		if (!file.exists()) throw new CompilerException("File " + parameters + " not found!");

		List<String> lines;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			throw new CompilerException(e);
		}
		compiler.getCurrentCompilingFile().getRawCode().addAll(lineNumber + 1, lines);
		return -1;
	}

}
