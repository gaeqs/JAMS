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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MIPS32AssemblerTest {

    @BeforeAll
    static void initRegistry() {
        Jams.initForTests();
    }

    @Test
    void testLabelWithoutAddress() {
        try {
            TestUtils.assemble("""
                        add $s0, $s0, $s0
                    test:
                    test2:
                    test3:
                    """);
        } catch (AssemblerException ex) {
            ex.printStackTrace();
            if (ex.getMessage().startsWith("Cannot assign an address to the following labels")) {
                return;
            }
            fail("Assembly failed, but the thrown exception was not the one expected.");
        }
        fail("Assembly was sucessful.");
    }

    @Test
    void testLabelWithoutAddress2() {
        try {
            TestUtils.assemble("""
                        add $s0, $s0, $s0
                    test: .align 0
                    """);
        } catch (AssemblerException ex) {
            ex.printStackTrace();
            if (ex.getMessage().startsWith("Cannot assign an address to the following labels")) {
                return;
            }
            fail("Assembly failed, but the thrown exception was not the one expected.");
        }
        fail("Assembly was sucessful.");
    }


    @Test
    void testEqv() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE, """
                .text
                .eqv TEST addi
                TEST $s0, $s0, 5
                tnei $s0, 5
                """);
        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testMacroInsideMacro() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE, """
                .text
                .macro a ()
                .macro b ()
                addi $s0, $s0, 100
                .endmacro
                b ()
                b ()
                .endmacro
                                
                a ()
                tnei $s0, 200
                """);
        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testEqvInsideMacro() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE, """
                .macro a ()
                .eqv fail addi
                .eqv addiu fail
                .macro b ()
                fail $s0, $s0, 100
                .endmacro
                b ()
                b ()
                .endmacro
                a ()
                addiu $s0, $s0, 100
                tnei $s0, 300
                """);
        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testNextLabel() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE, """
                    .macro a ()
                fail:
                    teq $s0, $s0
                    .endmacro
                    b +
                    a ()
                next:
                    add $s0, $s0, $s0
                """);
        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }

    @Test
    void testNextLabelNotFound() {
        try {
            TestUtils.assemble("""
                        .macro a ()
                        b +
                        .endmacro
                        a ()
                    next:
                        add $s0, $s0, $s0
                    """);
        } catch (AssemblerException ex) {
            ex.printStackTrace();
            if (ex.getMessage().endsWith("Cannot find label +!")) {
                return;
            }
            fail("Assembly failed, but the thrown exception was not the one expected.");
        }
        fail("Assembly was sucessful.");
    }

    @Test
    void testNextLabelNotFound2() {
        try {
            TestUtils.assemble("""
                    b +
                    """);
        } catch (AssemblerException ex) {
            ex.printStackTrace();
            if (ex.getMessage().endsWith("Cannot find label +!")) {
                return;
            }
            fail("Assembly failed, but the thrown exception was not the one expected.");
        }
        fail("Assembly was sucessful.");
    }

    @Test
    void testPreviousLabel() throws InterruptedException {
        var simulation = TestUtils.generateSimulation(SingleCycleArchitecture.INSTANCE, """
                    .globl main
                previous:
                    b end
                    .macro a ()
                fail:
                    teq $s0, $s0
                    .endmacro
                    a ()
                main:
                    b -
                next:
                    teq $s0, $s0
                end:
                    add $s0, $zero, $s0
                """);
        simulation.executeAll();
        simulation.waitForExecutionFinish();
        assertEquals(0, simulation.getExitCode());
    }


    @Test
    void testLoop() {
        try {
            TestUtils.assemble("""
                    .macro a ()
                    .macro b ()
                        a ()
                    .endmacro
                        b ()
                    .endmacro
                    
                    a ()
                    
                    """);
        } catch (AssemblerException ex) {
            ex.printStackTrace();
            if (ex.getMessage().contains("Cyclic dependency found in macro")) {
                return;
            }
            fail("Assembly failed, but the thrown exception was not the one expected.");
        }
        fail("Assembly was sucessful.");
    }

}