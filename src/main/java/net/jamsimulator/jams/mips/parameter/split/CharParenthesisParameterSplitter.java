package net.jamsimulator.jams.mips.parameter.split;

public class CharParenthesisParameterSplitter implements ParameterSplitter {

	private final char c;

	public CharParenthesisParameterSplitter(char c) {
		this.c = c;
	}

	@Override
	public int[] split(String string) {
		int index = string.lastIndexOf(c);
		int first = string.lastIndexOf('(');
		int second = string.lastIndexOf(')');
		return new int[]{0, index,
				index + 1, first - index - 1,
				first + 1, second - first - 1};
	}
}
