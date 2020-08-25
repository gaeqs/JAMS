package net.jamsimulator.jams.mips.parameter.split;

public class CharParameterSplitter implements ParameterSplitter {

	private final char c;

	public CharParameterSplitter(char c) {
		this.c = c;
	}

	@Override
	public int[] split(String string) {
		int index = string.indexOf(c);
		return new int[]{0, index, index + 1, string.length() - index - 1};
	}
}
