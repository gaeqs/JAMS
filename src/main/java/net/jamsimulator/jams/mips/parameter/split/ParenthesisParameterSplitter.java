package net.jamsimulator.jams.mips.parameter.split;

public class ParenthesisParameterSplitter implements ParameterSplitter {

	public static final ParenthesisParameterSplitter INSTANCE = new ParenthesisParameterSplitter();

	private ParenthesisParameterSplitter() {
	}

	@Override
	public int[] split(String string) {
		int first = string.lastIndexOf('(');
		int second = string.lastIndexOf(')');

		return new int[]{0, first, first + 1, second - first - 1};
	}
}
