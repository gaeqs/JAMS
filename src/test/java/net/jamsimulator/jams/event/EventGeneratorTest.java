package net.jamsimulator.jams.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventGeneratorTest {

	static boolean received0 = false, received2 = false;

	@Test
	void test() {
		SimpleEventBroadcast caller = new SimpleEventBroadcast();
		caller.registerListeners(new TestListener());

		TestEvent testEvent = new TestEvent(5);
		caller.callEvent(testEvent);

		assertTrue(received0, "Listeners not called.");
		assertTrue(received2, "Test 2 not called.");
	}


	private static class TestListener {

		@Listener
		public void test0(TestEvent event) {
			received0 = true;
			assertEquals(5, event.getValue(), "Incorrect value");
			System.out.println("Test0");
		}

		@Listener(priority = 10, ignoreCancelled = false)
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

		private int value;

		public TestEvent(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}