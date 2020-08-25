package net.jamsimulator.jams.mips.parameter.split;

public class SimpleParameterSplitter implements ParameterSplitter {

	public static final SimpleParameterSplitter INSTANCE = new SimpleParameterSplitter();

	private SimpleParameterSplitter() {
	}

	@Override
	public int[] split(String string) {
		return new int[]{0, string.length()};
	}
}
