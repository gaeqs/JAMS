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

package net.jamsimulator.jams.mips.interrupt;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.Validate;

import java.util.TreeSet;

public class ExternalInterruptController {

    private final TreeSet<Integer> pendingInterrupts;
    private MIPSInterruptException softwareInterrupt;

    public ExternalInterruptController() {
        pendingInterrupts = new TreeSet<>();
    }

    public boolean hasPendingInterrupts() {
        return pendingInterrupts.size() > 0;
    }

    public boolean isRequestingInterrupts(MIPSSimulation<?> simulation) {
        return pendingInterrupts.size() > 0 && simulation.areMIPSInterruptsEnabled() &&
                simulation.getIPLevel() < pendingInterrupts.last();
    }

    public void addRequest(int level) {
        Validate.isTrue(level > 1 && level < 64,
                "Hardware levels must be between 2 and 63! (Value 1 is reserved for software instructions.)");
        pendingInterrupts.add(level);
    }

    public void addSoftwareRequest(MIPSInterruptException softwareInterrupt) {
        Validate.notNull(softwareInterrupt, "Interrupt cannot be null!");
        this.softwareInterrupt = softwareInterrupt;
        pendingInterrupts.add(1);
    }

    public MIPSInterruptException getSoftwareInterrupt() {
        return softwareInterrupt;
    }

    public int getRequestedIPL() {
        var value = pendingInterrupts.pollLast();
        return value == null ? 0 : value;
    }

    public void reset() {
        pendingInterrupts.clear();
        softwareInterrupt = null;
    }
}
