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

package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.mips.register.event.RegisterLockEvent;
import net.jamsimulator.jams.mips.register.event.RegisterUnlockEvent;
import net.jamsimulator.jams.utils.StringUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a register. A register stores a 32-bit value. If {@link #isModifiable()} is true,
 * then it also can be modified.
 * <p>
 * A register can have several name. Those names shouldn't have a "$" character at the beginning, as "$" represents
 * that the string after it is a register; the character isn't part of the name.
 */
public class Register {

    protected final Registers registers;

    protected final int identifier;
    protected final Set<String> names;
    protected final boolean modifiable;
    private final List<InstructionExecution<?, ?>> lockedBy;
    protected int value;
    protected int defaultValue;

    /**
     * Creates a register using an identifier and a list of names.
     *
     * @param registers  the {@link Registers register set} where this register is stored at.
     * @param identifier the identifies.
     * @param names      the names.
     */
    public Register(Registers registers, int identifier, String... names) {
        Validate.notNull(registers, "Registers cannot be null!");
        Validate.isTrue(names.length > 0, "A register must have at least one name!");
        this.registers = registers;
        this.identifier = identifier;
        this.names = new HashSet<>();
        this.names.addAll(Arrays.asList(names));
        this.value = defaultValue = 0;
        this.modifiable = true;
        this.lockedBy = new ArrayList<>(5);
    }

    /**
     * Creates a register using an identifier and a list of names.
     *
     * @param registers  the {@link Registers register set} where this register is stored at.
     * @param identifier the identifies.
     * @param names      the names.
     */
    public Register(Registers registers, int identifier, Collection<String> names) {
        Validate.notNull(registers, "Registers cannot be null!");
        Validate.isTrue(names.size() > 0, "A register must have at least one name!");
        this.registers = registers;
        this.identifier = identifier;
        this.names = new HashSet<>();
        this.names.addAll(names);
        this.value = defaultValue = 0;
        this.modifiable = true;
        this.lockedBy = new ArrayList<>(5);
    }

    /**
     * Creates a register using an identifier, a value and a list of names. If the boolean
     * 'modifiable' is false this register will be read-only.
     *
     * @param registers  the {@link Registers register set} where this register is stored at.
     * @param identifier the identifier.
     * @param value      the value.
     * @param modifiable whether this register is modifiable.
     * @param names      the names.
     */
    public Register(Registers registers, int identifier, int value, boolean modifiable, String... names) {
        Validate.notNull(registers, "Registers cannot be null!");
        Validate.isTrue(names.length > 0, "A register must have at least one name!");
        this.registers = registers;
        this.identifier = identifier;
        this.names = new HashSet<>();
        this.names.addAll(Arrays.asList(names));
        this.value = defaultValue = value;
        this.modifiable = modifiable;
        this.lockedBy = new ArrayList<>(5);
    }

    /**
     * Creates a register using an identifier, a value and a list of names. If the boolean
     * 'modifiable' is false this register will be read-only.
     *
     * @param registers  the {@link Registers register set} where this register is stored at.
     * @param identifier the identifier.
     * @param value      the value.
     * @param modifiable whether this register is modifiable.
     * @param names      the names.
     */
    public Register(Registers registers, int identifier, int value, boolean modifiable, Collection<String> names) {
        Validate.notNull(registers, "Registers cannot be null!");
        Validate.isTrue(names.size() > 0, "A register must have at least one name!");
        this.registers = registers;
        this.identifier = identifier;
        this.names = new HashSet<>();
        this.names.addAll(names);
        this.value = defaultValue = value;
        this.modifiable = modifiable;
        this.lockedBy = new ArrayList<>(5);
    }

    /**
     * Returns the {@link Registers register set} this register is inside.
     *
     * @return the {@link Registers register set}.
     */
    public Registers getRegisters() {
        return registers;
    }

    /**
     * Returns the identifier of the register.
     *
     * @return the identifier.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Returns an immutable {@link Set} with all this register's names.
     *
     * @return the {@link Set}.
     */
    public Set<String> getNames() {
        return new HashSet<>(names);
    }

    /**
     * Returns whether this register matches the given name.
     *
     * @param name the name
     * @return whether this register matches the given name.
     */
    public boolean hasName(String name) {
        return names.contains(name);
    }

    /**
     * Returns whether this register is locked by any execution.
     *
     * @return whether this register is locked.
     */
    public boolean isLocked() {
        return lockedBy.size() > 0;
    }

    /**
     * Returns whether this register is locked by and only by the given execution.
     *
     * @param execution the execution.
     * @return whether this register is locked by and only by the given execution.
     */
    public boolean isLockedOnlyBy(InstructionExecution<?, ?> execution) {
        return lockedBy.size() == 1 && execution.equals(lockedBy.get(0));
    }

    /**
     * Returns whether this register is locked by some execution older than the execution of the given id.
     *
     * @param id the id of the execution.
     * @return whether this register is locked.
     */
    public boolean isLockedBeforeId(long id) {
        return !lockedBy.isEmpty() && (!(lockedBy.get(0) instanceof MultiCycleExecution<?, ?> ex)
                || ex.getInstructionId() < id);
    }

    /**
     * Returns whether the given execution is in the last position of the lock queue.
     * If true, the execution may perform forwarding.
     *
     * @param execution the execution.
     * @return whether this execution is in the last position of the lock queue.
     */
    public boolean isLastLocked(InstructionExecution<?, ?> execution) {
        if (lockedBy.isEmpty()) return false;
        return lockedBy.get(lockedBy.size() - 1).equals(execution);
    }


    /**
     * Returns whether the given execution is in the last position of the lock queue.
     * If true, the execution may perform forwarding.
     *
     * @param execution the execution.
     * @return whether this execution is in the last position of the lock queue.
     */
    public boolean isLastLockedBeforeId(InstructionExecution<?, ?> execution, long id) {
        if (lockedBy.isEmpty()) return false;

        var iterator = lockedBy.listIterator(lockedBy.size());
        while (iterator.hasPrevious()) {
            var current = iterator.previous();
            if (current instanceof MultiCycleExecution<?, ?> ex && ex.getInstructionId() < id) {
                return current.equals(execution);
            }
        }
        return false;
    }

    /**
     * Returns whether the given execution is in the penultimate position of the lock queue.
     * If true, the execution may perform forwarding.
     *
     * @param execution the execution.
     * @return whether this execution is in the penultimate position of the lock queue.
     */
    public boolean isPenultimateLocked(InstructionExecution<?, ?> execution) {
        if (lockedBy.size() < 2) return false;
        return lockedBy.get(lockedBy.size() - 2).equals(execution);
    }

    /**
     * Returns whether the given execution is in the first position of the lock queue.
     * If true, the execution may enter the memory step.
     *
     * @param execution the execution.
     * @return whether this execution is in the first position of the lock queue.
     */
    public boolean isFirstLocked(InstructionExecution<?, ?> execution) {
        if (lockedBy.isEmpty()) return false;
        return lockedBy.get(0).equals(execution);
    }

    public void lock(InstructionExecution<?, ?> execution) {
        if (lockedBy.contains(execution)) return;
        if (registers.eventCallsEnabled) {
            var before = registers.callEvent(new RegisterLockEvent.Before(this, execution));
            if (before.isCancelled()) return;
            execution = before.getExecution();
            lockedBy.add(execution);
            registers.callEvent(new RegisterLockEvent.After(this, execution));
        } else {
            lockedBy.add(execution);
        }
    }

    public void unlock(InstructionExecution<?, ?> execution) {
        if (registers.eventCallsEnabled) {
            var before = registers.callEvent(new RegisterUnlockEvent.Before(this, execution));
            if (before.isCancelled()) return;
            execution = before.getExecution();
            if (!lockedBy.remove(execution)) return;
            registers.callEvent(new RegisterUnlockEvent.After(this, execution));
        } else {
            lockedBy.remove(execution);
        }
    }

    public void unlock() {
        lockedBy.clear();
    }

    /**
     * This method should be used exclusively to undo steps.
     * This method doesn't call any event.
     * <p>
     * Locks this register and sets the execution at the first position.
     *
     * @param execution the execution.
     */
    public void lockFirst(InstructionExecution<?, ?> execution) {
        lockedBy.add(0, execution);
    }

    /**
     * This method should be used exclusively to undo steps.
     * This method doesn't call any event.
     * <p>
     * Unlocks this register, removing the last occurrence of the given execution.
     *
     * @param execution the execution.
     */
    public void unlockMostRecent(InstructionExecution<?, ?> execution) {
        for (int i = lockedBy.size() - 1; i >= 0; i--) {
            if (execution.equals(lockedBy.get(i))) {
                lockedBy.remove(i);
                return;
            }
        }
    }

    public String printLockingExecutions() {
        return lockedBy.stream().map(it ->
                "0x" + StringUtils.addZeros(Integer.toHexString(it.getAddress()), 8)).toList().toString();
    }

    /**
     * Returns the value stored in this register.
     *
     * @return the value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value stored in the register.
     * If this register is not modifiable this method will do nothing.
     *
     * @param value the value.
     * @see #isModifiable()
     */
    public void setValue(int value) {
        if (!modifiable) return;
        if (!registers.eventCallsEnabled) {
            setValue0(value);
            return;
        }

        RegisterChangeValueEvent.Before before = registers.callEvent(
                new RegisterChangeValueEvent.Before(this, this.value, value));
        if (before.isCancelled()) return;

        int old = this.value;
        setValue0(before.getNewValue());

        registers.callEvent(new RegisterChangeValueEvent.After(this, old, this.value));
    }

    /**
     * Returns the bit at the given position.
     *
     * @param position the position.
     * @return the bit.
     */
    public boolean getBit(int position) {
        return ((value >> position) & 1) == 1;
    }

    /**
     * Returns the n-bits located at the given posiiton.
     *
     * @param from   the start of the array.
     * @param length the length.
     * @return the bits.
     */
    public int getSection(int from, int length) {
        return (value >> from) & ((1 << length) - 1);
    }

    /**
     * Sets the new value, without any event call.
     * <p>
     * This event is called by {@link #setValue(int)} to se the new register's value.
     *
     * @param value the value.
     */
    protected void setValue0(int value) {
        this.value = value;
    }

    /**
     * Returns whether this register is modifiable.
     *
     * @return whether this register is modifiable.
     */
    public boolean isModifiable() {
        return modifiable;
    }

    /**
     * Makes the current value the default value.
     * When the method {@link #reset()} is invoked the current value
     * will be set to the current value when this method is used.
     */
    public void makeCurrentValueAsDefault() {
        defaultValue = value;
    }

    /**
     * Sets this register's value to its initial state.
     */
    public void reset() {
        setValue(defaultValue);
        lockedBy.clear();
    }

    /**
     * Creates a copy of the register.
     *
     * @param registers the {@link Registers register set} the copy will be stored at.
     * @return the copy.
     */
    public Register copy(Registers registers) {
        Register register = new Register(registers, identifier, value, modifiable, names);
        register.defaultValue = defaultValue;
        return register;
    }
}
