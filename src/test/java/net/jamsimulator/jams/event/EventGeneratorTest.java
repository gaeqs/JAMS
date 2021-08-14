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

package net.jamsimulator.jams.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventGeneratorTest {

	static boolean received0 = false, received2 = false;

	@Test
	void test() {
		SimpleEventBroadcast caller = new SimpleEventBroadcast();
		caller.registerListeners(new TestListener(), false);

		TestEvent testEvent = new TestEvent(5);
		caller.callEvent(testEvent);

		assertTrue(received0, "Listeners not called.");

		//Test2 shouldn't be called.
		assertFalse(received2, "Test 2 is called.");
	}


	private static class TestListener {

		@Listener
		public void test0(TestEvent event) {
			received0 = true;
			assertEquals(5, event.getValue(), "Incorrect value");
			System.out.println("Test0");
		}

		@Listener(priority = 10)
		public void test1(TestEvent event) {
			assertFalse(received0, "Test0 called first!");
			System.out.println("Test1");
		}

		@Listener
		public void test2(Event event) {
			received2 = true;
		}
	}

	private static class TestEvent extends Event {

		private final int value;

		public TestEvent(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}