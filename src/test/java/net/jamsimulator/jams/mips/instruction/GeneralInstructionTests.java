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

package net.jamsimulator.jams.mips.instruction;

class GeneralInstructionTests {
/*
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
    @Test
    void testBasicInstruction() {

        Registers set = simulation.getRegisters();
        Register t0 = set.getRegister("t0").get();
        Register t1 = set.getRegister("t1").get();
        Register t2 = set.getRegister("t2").get();
        t0.setValue(3);
        t1.setValue(20);

        List<ParameterType>[] list = new List[]{Collections.singletonList(ParameterType.REGISTER),
                Collections.singletonList(ParameterType.REGISTER),
                Collections.singletonList(ParameterType.REGISTER)};

        Optional<Instruction> optional = simulation.getInstructionSet().getBestCompatibleInstruction("add", list);

        if (optional.isEmpty()) fail("Instruction not found.");

        ParameterParseResult[] parameters = new ParameterParseResult[]{
                ParameterParseResult.builder().register(t2.getIdentifier()).build(),
                ParameterParseResult.builder().register(t1.getIdentifier()).build(),
                ParameterParseResult.builder().register(t0.getIdentifier()).build()
        };

        AssembledInstruction[] instructions = optional.get().assemble(null, 0, parameters);
        if (instructions.length != 1) fail("Incorrect instruction.");


        InstructionExecution<?, ?> execution = instructions[0].getBasicOrigin()
                .generateExecution(simulation, instructions[0], 0).orElse(null);

        assertTrue(execution instanceof SingleCycleExecution, "Execution is not a single cycle execution.");

        ((SingleCycleExecution<?>) execution).execute();
        assertEquals(23, t2.getValue(), "Bad add instruction result.");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void testOverflow() {
        Registers set = simulation.getRegisters();
        Register t0 = set.getRegister("t0").get();
        Register t1 = set.getRegister("t1").get();
        Register t2 = set.getRegister("t2").get();
        t0.setValue(Integer.MAX_VALUE);
        t1.setValue(20);

        ParameterParseResult[] parameters = new ParameterParseResult[]{
                ParameterParseResult.builder().register(t2.getIdentifier()).build(),
                ParameterParseResult.builder().register(t1.getIdentifier()).build(),
                ParameterParseResult.builder().register(t0.getIdentifier()).build()
        };

        InstructionAdd add = new InstructionAdd();
        AssembledInstruction instruction = add.assembleBasic(parameters);
        InstructionExecution<?, ?> execution = add.generateExecution(simulation, instruction, 0).orElse(null);

        assertTrue(execution instanceof SingleCycleExecution, "Execution is not a single cycle execution.");

        try {
            ((SingleCycleExecution<?>) execution).execute();
            fail("Execution didn't throw an exception.");
        } catch (MIPSInterruptException ex) {
            assertEquals(ex.getInterruptCause(), InterruptCause.ARITHMETIC_OVERFLOW_EXCEPTION, "Exception caught, but it's not an Integer Overflow exception.");
        }
    }*/
}