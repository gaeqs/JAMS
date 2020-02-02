package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import net.jamsimulator.jams.mips.instruction.compiled.CompiledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.RuntimeInstructionException;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Mips32Memory;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class GeneralInstructionTests {

	static Simulation simulation = new Simulation(new InstructionSet(true, true, true),
			new MIPS32RegisterSet(), new Mips32Memory());

	@Test
	void testBasicInstruction() {
		RegisterSet set = simulation.getRegisterSet();
		Register t0 = set.getRegister("t0").get();
		Register t1 = set.getRegister("t1").get();
		Register t2 = set.getRegister("t2").get();
		t0.setValue(3);
		t1.setValue(20);

		CompiledInstruction instruction = new InstructionAdd().compileBasic(new Object[]
				{t2.getIdentifier(), t1.getIdentifier(), t0.getIdentifier()});
		instruction.execute(simulation);
		assertEquals(23, t2.getValue(), "Bad add instruction result.");

	}

	@Test
	void testOverflow() {
		RegisterSet set = simulation.getRegisterSet();
		Register t0 = set.getRegister("t0").get();
		Register t1 = set.getRegister("t1").get();
		Register t2 = set.getRegister("t2").get();
		t0.setValue(Integer.MAX_VALUE);
		t1.setValue(20);

		CompiledInstruction instruction = new InstructionAdd().compileBasic(new Object[]
				{t2.getIdentifier(), t1.getIdentifier(), t0.getIdentifier()});

		try {
			instruction.execute(simulation);
			fail("Execution didn't throw an exception.");
		} catch (RuntimeInstructionException ex) {
			assertEquals(ex.getMessage(), "Integer overflow.", "Exception caught, but it's not an Integer Overflow exception.");
		}
	}
}