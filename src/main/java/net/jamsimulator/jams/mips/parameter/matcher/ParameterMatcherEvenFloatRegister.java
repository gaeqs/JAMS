package net.jamsimulator.jams.mips.parameter.matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ParameterMatcherEvenFloatRegister implements Predicate<String> {

	private static final int FLOAT_REGISTERS = 32;

	private static List<String> possibilities = new LinkedList<>();

	static {
		//Loads all possible registers into a linked list
		possibilities = new LinkedList<>();

		for (int i = 0; i < FLOAT_REGISTERS; i += 2) {
			possibilities.add("$f" + i);
		}
	}

	@Override
	public boolean test(String s) {
		return possibilities.contains(s);
	}
}
