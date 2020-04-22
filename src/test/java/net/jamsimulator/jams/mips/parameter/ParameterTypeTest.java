package net.jamsimulator.jams.mips.parameter;

import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Registers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterTypeTest {

	@Test
	void testParameterTypes() {
		Registers set = new MIPS32Registers();
		for (ParameterType value : ParameterType.values()) {
			assertTrue(value.match(value.getExample(), set), "Example for parameter type " + value + " doesn't match.");
			assertTrue(ParameterType.getCompatibleParameterTypes(value.getExample(), set).contains(value),
					"Couldn't found the parameter using the example.");
			System.out.println(value + ": " + value.getExample() + " -> " + value.parse(value.getExample(), set));
		}
	}

}