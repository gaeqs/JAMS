package net.jamsimulator.jams.mips.assembler.directive;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents a directive. Directive are the direct equivalent to the preprocessor code in C.
 * They are used to give orders to the assembler.
 */
public abstract class Directive {

	private final String name;


	public Directive(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	/**
	 * Returns the name of this directive.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Executes the directive in the given assembler.
	 *
	 * @param lineNumber the line number the directive is at.
	 * @param line       the line of the directive.
	 * @param parameters the parameters of the directive.
	 * @param assembler  the assembler.
	 * @return the amount of bytes that have been allocated for this directive.
	 */
	public abstract int execute(int lineNumber, String line, String[] parameters, Assembler assembler);

	/**
	 * This method is executed after all labels, instructions and directives had been decoded.
	 *
	 * @param parameters the parameters of the directive.
	 * @param assembler  the assembler.
	 * @param file       the file where the directive is located at.
	 * @param lineNumber the line number the directive is at.
	 * @param address    the start of the memory address dedicated to this directive in the method {@link #execute(int, String, String[], Assembler)}.
	 */
	public abstract void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address);


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Directive directive = (Directive) o;
		return name.equals(directive.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
