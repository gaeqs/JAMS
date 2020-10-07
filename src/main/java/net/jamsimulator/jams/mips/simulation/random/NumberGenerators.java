package net.jamsimulator.jams.mips.simulation.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Represents the collection of number generators of a simulation.
 */
public class NumberGenerators {

	private final Map<Integer, Random> generators;

	/**
	 * Creates the collection of number generations.
	 */
	public NumberGenerators() {
		generators = new HashMap<>();
	}

	/**
	 * Returns the generation that matches the given index.
	 * If the generation is not found, a new one is created.
	 *
	 * @param index the index.
	 * @return the generator.
	 */
	public Random getGenerator(int index) {
		var generator = generators.get(index);
		if (generator == null) {
			generator = new Random();
			generators.put(index, generator);
		}
		return generator;
	}
}
