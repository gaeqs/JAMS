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
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.MultiALUPipelinedArchitecture;
import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class InstructionExtTest {

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
    void test(String architecture) throws InterruptedException {
        var arch = Manager.of(Architecture.class).get(architecture).orElseThrow();
        var simulation = TestUtils.generateSimulation(arch,
                """
                        .text
                        li $s0, 0b1011110000101010
                        li $s1, 0b101111
                        ext $s0, $s0, 10, 6
                        tne $s0, $s0
                        """
        );

        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testCompilationFail() {
        try {
            TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE,
                    """
                            .text
                            li $s0, 0b1011110000101010
                            li $s1, 0b101111
                            ext $s0, $s0, 10, 0
                            tne $s0, $s0
                            """
            );
        } catch (AssemblerException exception) {
            // SUCCESS
            return;
        }
        fail("Code compiled successfully.");
    }
}