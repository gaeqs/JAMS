package net.jamemulator.jams.mips.parameter.matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ParameterMatcherRegister implements Predicate<String> {

	private static final int NUMERIC_REGISTERS = 32;
	private static final int OUT_REGISTERS = 2;
	private static final int ARGUMENT_REGISTERS = 4;
	private static final int TEMPORAL_REGISTERS = 10;
	private static final int SAFE_REGISTERS = 8;
	private static final int KERNEL_REGISTERS = 2;

	private static List<String> possibilities = new LinkedList<>();

	static {
		//Loads all possible registers into a linked list
		possibilities = new LinkedList<>();
		possibilities.add("$zero");
		possibilities.add("$at");
		possibilities.add("$gp");
		possibilities.add("$sp");
		possibilities.add("$fp");
		possibilities.add("$ra");

		for (int i = 0; i < NUMERIC_REGISTERS; i++) {
			possibilities.add("$" + i);
		}
		for (int i = 0; i < OUT_REGISTERS; i++) {
			possibilities.add("$v" + i);
		}
		for (int i = 0; i < ARGUMENT_REGISTERS; i++) {
			possibilities.add("$a" + i);
		}
		for (int i = 0; i < TEMPORAL_REGISTERS; i++) {
			possibilities.add("$t" + i);
		}
		for (int i = 0; i < SAFE_REGISTERS; i++) {
			possibilities.add("$s" + i);
		}
		for (int i = 0; i < KERNEL_REGISTERS; i++) {
			possibilities.add("$" + i);
		}
	}

	@Override
	public boolean test(String s) {
		return possibilities.contains(s);
	}
}
