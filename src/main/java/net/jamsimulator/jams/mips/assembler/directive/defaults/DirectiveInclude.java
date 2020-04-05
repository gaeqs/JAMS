package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

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
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have one parameter.");


		File file = new File(parameters[0]);
		if (!file.exists()) throw new AssemblerException("File " + parameters + " not found!");

		List<String> lines;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			throw new AssemblerException(e);
		}
		assembler.getCurrentAssemblingFile().getRawCode().addAll(lineNumber + 1, lines);
		return -1;
	}

	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {

	}
}
