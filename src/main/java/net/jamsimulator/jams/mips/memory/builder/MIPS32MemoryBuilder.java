package net.jamsimulator.jams.mips.memory.builder;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.Mips32Memory;

public class MIPS32MemoryBuilder extends MemoryBuilder {

	public static final String NAME = "MIPS32";

	/**
	 * Creates a MIPS32 memory builder.
	 */
	public MIPS32MemoryBuilder() {
		super(NAME);
	}

	@Override
	public Memory createMemory() {
		return new Mips32Memory();
	}
}
