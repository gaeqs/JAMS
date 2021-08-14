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

package net.jamsimulator.jams.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
class BagTest {

	@Test
	void amount() {
		Bag<Integer> bag = new Bag<>();
		bag.add(1);
		bag.add(1);
		assertEquals(2, bag.amount(1), "Amount is not equals.");
	}

	@Test
	void removeAllAmounts() {
		Bag<Integer> bag = new Bag<>();
		bag.add(1);
		bag.add(1);
		bag.removeAllAmounts(1);
		assertEquals(0, bag.amount(1), "Amount is not zero.");
	}

	@Test
	void size() {
		Bag<Integer> bag = new Bag<>();
		bag.add(1);
		bag.add(1);
		bag.add(3);
		assertEquals(2, bag.size());
	}

	@Test
	void amount0() {
		Bag<Integer> bag = new Bag<>();
		bag.add(1);
		bag.add(1);
		bag.add(3);
		assertEquals(3, bag.amount());
	}

	@Test
	void isEmpty() {
		Bag<Integer> bag = new Bag<>();
		assertTrue(bag.isEmpty(), "Bag is not empty.");
		bag.add(1);
		assertFalse(bag.isEmpty(), "Bag is empty.");
		bag.add(2);
		bag.clear();
		assertTrue(bag.isEmpty(), "Bag is not empty.");
	}

	@Test
	void contains() {
		Bag<Integer> bag = new Bag<>();
		assertFalse(bag.contains(1), "Bag contains element.");
		bag.add(1);
		assertTrue(bag.contains(1), "Bag doesn't contain element.");
		bag.add(1);
		bag.removeAllAmounts(1);
		assertFalse(bag.contains(1), "Bag contains element.");
	}

	@Test
	void iterator() {
		Bag<Integer> bag = new Bag<>();
		bag.add(3);
		bag.add(5);
		bag.add(2);

		List<Integer> contains = new ArrayList<>();

		Iterator<Integer> iterator = bag.iterator();
		while (iterator.hasNext()) {
			contains.add(iterator.next());
		}

		assertTrue(contains.contains(3), "Element not found.");
		assertTrue(contains.contains(5), "Element not found.");
		assertTrue(contains.contains(2), "Element not found.");
	}

	@Test
	void add() {
		Bag<Integer> bag = new Bag<>();
		bag.add(3);
		assertTrue(bag.contains(3), "Elements is not present.");
		assertFalse(bag.isEmpty(), "Bag is empty.");
		assertEquals(1, bag.size(), "Size is not 1.");
		bag.add(3);
		assertEquals(1, bag.size(), "Size is not 1.");
		assertEquals(2, bag.amount(), "Amount is not 2.");
	}

	@Test
	void remove() {
		Bag<Integer> bag = new Bag<>();
		bag.add(2);
		bag.remove(2);

		assertTrue(bag.isEmpty(), "Bag is not empty.");
	}

	@Test
	void containsAll() {
		Bag<Integer> bag = new Bag<>();
		bag.add(2);
		bag.add(6);
		bag.add(1);

		assertTrue(bag.containsAll(Arrays.asList(1, 2, 6)), "ContainsAll failed.");
	}

	@Test
	void addAll() {
		Bag<Integer> bag = new Bag<>();
		bag.addAll(Arrays.asList(2, 4, 5, 3, 7, 2));
		assertTrue(bag.containsAll(Arrays.asList(2, 3, 4, 5, 7)), "AddAll failed.");
		assertEquals(6, bag.amount(), "Amount is not 6.");
	}

	@Test
	void removeAll() {
		Bag<Integer> bag = new Bag<>();
		bag.addAll(Arrays.asList(2, 3, 6, 8, 5, 2, 2));
		bag.removeAll(Arrays.asList(2, 3, 6, 9));
		assertEquals(3, bag.size(), "Size is not 3.");
		assertEquals(4, bag.amount(), "Amount is not 4.");
	}

	@Test
	void retainAll() {
		Bag<Integer> bag = new Bag<>();
		bag.addAll(Arrays.asList(2, 3, 6, 8, 5, 2, 2));
		bag.retainAll(Arrays.asList(2, 3, 6, 9));
		assertEquals(3, bag.size(), "Size is not 5.");
		assertEquals(5, bag.amount(), "Amount is not 5.");
	}

	@Test
	void clear() {
		Bag<Integer> bag = new Bag<>();
		bag.addAll(Arrays.asList(2, 3, 6, 8, 5, 2, 2));
		bag.clear();
		assertTrue(bag.isEmpty(), "Bag is not empty.");
	}

}