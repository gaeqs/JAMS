/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
