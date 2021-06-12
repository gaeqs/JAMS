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

package net.jamsimulator.jams.mips.register.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.register.Register;

/**
 * This event is called when a register is unlocked.
 */
public class RegisterUnlockEvent extends Event {

    protected final Register register;
    protected InstructionExecution<?, ?> execution;

    protected RegisterUnlockEvent(Register register, InstructionExecution<?, ?> execution) {
        this.register = register;
        this.execution = execution;
    }

    public Register getRegister() {
        return register;
    }

    public InstructionExecution<?, ?> getExecution() {
        return execution;
    }

    public static class Before extends RegisterUnlockEvent implements Cancellable {

        private boolean cancelled;

        public Before(Register register, InstructionExecution<?, ?> execution) {
            super(register, execution);
            this.cancelled = false;
        }

        public void setExecution(InstructionExecution<?, ?> execution) {
            this.execution = execution;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After extends RegisterUnlockEvent {

        public After(Register register, InstructionExecution<?, ?> execution) {
            super(register, execution);
        }
    }
}
