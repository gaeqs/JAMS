package net.jamsimulator.jams.mips.parameter.split;

public class CharParenthesisParameterSplitter implements ParameterSplitter {

	private final char c;

	public CharParenthesisParameterSplitter(char c) {
		this.c = c;
	}

	@Override
	public int[] split(String string) {
		int index = string.indexOf(c);
		int first = string.indexOf('(');
		int second = string.indexOf(')');
		return new int[]{0, index, index + 1, first, first + 1, second - first - 1};
	}
}
