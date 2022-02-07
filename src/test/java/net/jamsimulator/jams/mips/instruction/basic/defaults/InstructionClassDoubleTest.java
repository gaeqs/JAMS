/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionClassDoubleTest {

    @BeforeAll
    static void initRegistry() {
        Jams.initForTests();
    }

    @Test
    void testNaN() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, NaN
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b10
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testNegativeInfinity() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, -Infinity
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b100
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testNegativeNormal() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, -5.3448
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b1000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testNegativeSubnormal() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, -2.22507385850720088902458687609E-308
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b10000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testNegativeZero() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, -0.0
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b100000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testPositiveInfinity() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, Infinity
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b1000000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testPositiveNormal() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, 865.65
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b10000000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testPositiveSubnormal() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, 2.22507385850720088902458687609E-308
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b100000000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testPositiveZero() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                """
                        .text
                        lid $f0, 0.0
                        class.d $f0, $f0
                        mfc1 $s0, $f0
                        tnei $s0, 0b1000000000
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }



}