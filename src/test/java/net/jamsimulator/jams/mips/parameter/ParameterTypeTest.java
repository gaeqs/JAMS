package net.jamsimulator.jams.mips.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterTypeTest {

	@Test
	void testParameterTypes() {
		for (ParameterType value : ParameterType.values()) {
			assertTrue(value.match(value.getExample()), "Example for parameter type " + value + " doesn't match.");
			assertTrue(ParameterType.getCompatibleParameterTypes(value.getExample()).contains(value),
					"Couldn't found the parameter using the example.");
		}
	}

}