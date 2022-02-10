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

package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionAddTest {

    @BeforeAll
    static void initRegistry() {
        Jams.initForTests();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            SingleCycleArchitecture.NAME,
            MultiCycleArchitecture.NAME,
            MultiALUPipelinedArchitecture.NAME
    })
    void testSimpleAdition(String architecture) throws InterruptedException {
        var arch = Manager.of(Architecture.class).get(architecture).orElseThrow();
        var simulation = TestUtils.generateSimulation(arch,
                """
                        .text
                        li $s0, 20
                        li $s1, 45
                        add $s2, $s0, $s1
                        add $s3, $s1, $s0
                        tnei $s2, 65
                        tnei $s3, 65
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            SingleCycleArchitecture.NAME,
            MultiCycleArchitecture.NAME,
            MultiALUPipelinedArchitecture.NAME
    })
    void testOverflow(String architecture) throws InterruptedException {
        var arch = Manager.of(Architecture.class).get(architecture).orElseThrow();
        var simulation = TestUtils.generateSimulation(arch,
                """
                        .text
                        li $s0, 0x7FFFFFFF
                        li $s1, 2
                        add $s2, $s0, $s1
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(
                0x1000 + InterruptCause.ARITHMETIC_OVERFLOW_EXCEPTION.getValue(),
                simulation.getExitCode()
        );
    }
}