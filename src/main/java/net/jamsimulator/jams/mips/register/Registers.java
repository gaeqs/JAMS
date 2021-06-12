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

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a {@link Register} set. An instance of this class stores all
 * {@link Register}s used by a {@link MIPSSimulation}.
 * <p>
 * Registers ProgramCounter, HighRegister and LowRegister are always present.
 */
public class Registers extends SimpleEventBroadcast {

    protected final Set<Character> validRegistersStarts;

    protected Register[] registers;
    protected Register[][] coprocessor0Registers;
    protected Register[] coprocessor1Registers;

    protected Register programCounter;

    protected boolean eventCallsEnabled;

    /**
     * Creates a new Register set using the general registers, the coprocessor 0 registers and the coprocessor 1 registers.
     * If any of the {@link Set}s is null, the method will create a {@link HashSet} for the parameter.
     * <p>
     * Remember: ProgramCounter, High and Low registers are automatically created.
     *
     * @param registers             the general registers.
     * @param coprocessor0Registers the coprocessor 0 registers.
     * @param coprocessor1Registers the coprocessor 1 registers.
     */
    public Registers(Set<Character> validRegistersStarts, Register[] registers,
                     Register[][] coprocessor0Registers, Register[] coprocessor1Registers) {
        Validate.notNull(validRegistersStarts, "Valid registers starts cannot be null!");
        this.validRegistersStarts = validRegistersStarts;
        this.registers = registers == null ? new Register[32] : registers;
        this.coprocessor0Registers = coprocessor0Registers == null ? new Register[32][32] : coprocessor0Registers;
        this.coprocessor1Registers = coprocessor1Registers == null ? new Register[32] : coprocessor1Registers;
        loadEssentialRegisters();
        this.eventCallsEnabled = true;
    }

    /**
     * Returns the {@link Set} of valid registers' starts.
     * This is is unmodifiable.
     * <p>
     * Every register should start using any of these characters.
     *
     * @return the {@link Set}
     */
    public Set<Character> getValidRegistersStarts() {
        return Collections.unmodifiableSet(validRegistersStarts);
    }

    /**
     * Returns the program counter {@link Register}.
     *
     * @return the program counter.
     */
    public Register getProgramCounter() {
        return programCounter;
    }

    /**
     * Returns an unmodifiable {@link Set} with all registers inside this set.
     *
     * @return the {@link Set}.
     */
    public Set<Register> getRegisters() {
        Set<Register> registers = new HashSet<>();
        registers.add(programCounter);

        for (Register register : this.registers) {
            if (register != null) registers.add(register);
        }
        for (Register[] array : coprocessor0Registers) {
            for (Register register : array) {
                if (register != null) registers.add(register);
            }
        }
        for (Register register : coprocessor1Registers) {
            if (register != null) registers.add(register);
        }

        return Collections.unmodifiableSet(registers);
    }

    /**
     * Returns an unmodifiable {@link Set} with all general registers inside this set.
     *
     * @return the {@link Set}.
     */
    public Set<Register> getGeneralRegisters() {
        Set<Register> registers = new HashSet<>();
        for (Register register : this.registers) {
            if (register != null) registers.add(register);
        }
        return Collections.unmodifiableSet(registers);
    }

    /**
     * Returns an unmodifiable {@link Set} with all COP0 registers inside this set.
     *
     * @return the {@link Set}.
     */
    public Set<Register> getCoprocessor0Registers() {
        Set<Register> registers = new HashSet<>();
        for (Register[] array : coprocessor0Registers) {
            for (Register register : array) {
                if (register != null) registers.add(register);
            }

        }
        return Collections.unmodifiableSet(registers);
    }

    /**
     * Returns an unmodifiable {@link Set} with all COP1 registers inside this set.
     *
     * @return the {@link Set}.
     */
    public Set<Register> getCoprocessor1Registers() {
        Set<Register> registers = new HashSet<>();
        for (Register register : coprocessor1Registers) {
            if (register != null) registers.add(register);
        }
        return Collections.unmodifiableSet(registers);
    }

    /**
     * Get the general {@link Register} whose name matches the given string, if present.
     * <p>
     * This method should not be used on execution, as it consumes resources.
     *
     * @param name the name.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getRegister(String name) {
        for (Register register : registers) {
            if (register == null) continue;
            if (register.hasName(name)) return Optional.of(register);
        }
        return Optional.empty();
    }

    /**
     * Get the general {@link Register} whose identifier matches the given int, if present.
     *
     * @param identifier the identifier.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getRegister(int identifier) {
        if (identifier < 0 || identifier > registers.length) return Optional.empty();
        return Optional.ofNullable(registers[identifier]);
    }

    /**
     * Returns the general {@link Register} whose identifier matches the given int.
     * This method is unchecked: returns the register the faster way possible without checking anything before.
     * This may cause several exceptions.
     *
     * @param identifier the identifier.
     * @return the register.
     */
    public Register getRegisterUnchecked(int identifier) {
        return registers[identifier];
    }

    /**
     * Get the coprocessor 0 {@link Register} whose name matches the given string, if present.
     * <p>
     * This method should not be used on execution, as it consumes resources.
     *
     * @param name the name.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getCoprocessor0Register(String name) {
        for (Register[] registers : coprocessor0Registers) {
            for (Register register : registers) {
                if (register == null) continue;
                if (register.hasName(name)) return Optional.of(register);
            }
        }
        return Optional.empty();
    }


    /**
     * Get the coprocessor 0 {@link Register} whose identifier matches the given int, if present.
     *
     * @param identifier the identifier.
     * @param sel        the  sub-index.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getCoprocessor0Register(int identifier, int sel) {
        return Optional.ofNullable(coprocessor0Registers[identifier][sel]);
    }

    /**
     * Returns the coprocessor 0 {@link Register} whose identifier matches the given int.
     * This method is unchecked: returns the register the faster way possible without checking anything before.
     * This may cause several exceptions.
     *
     * @param identifier the identifier (set).
     * @param sel        the  sub-index.
     * @return the register.
     */
    public Register getCoprocessor0RegisterUnchecked(int identifier, int sel) {
        return coprocessor0Registers[identifier][sel];
    }


    /**
     * Get the coprocessor 1 {@link Register} whose name matches the given string, if present.
     * <p>
     * This method should not be used on execution, as it consumes resources.
     *
     * @param name the name.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getCoprocessor1Register(String name) {
        for (Register register : coprocessor1Registers) {
            if (register == null) continue;
            if (register.hasName(name)) return Optional.of(register);
        }
        return Optional.empty();
    }

    /**
     * Get the coprocessor 1 {@link Register} whose identifier matches the given int, if present.
     *
     * @param identifier the identifier.
     * @return the {@link Register}, if present.
     */
    public Optional<Register> getCoprocessor1Register(int identifier) {
        return Optional.ofNullable(coprocessor1Registers[identifier]);
    }

    /**
     * Returns the coprocessor 1 {@link Register} whose identifier matches the given int.
     * This method is unchecked: returns the register the faster way possible without checking anything before.
     * This may cause several exceptions.
     *
     * @param identifier the identifier.
     * @return the register.
     */
    public Register getCoprocessor1RegisterUnchecked(int identifier) {
        return coprocessor1Registers[identifier];
    }

    /**
     * Unlocks all registers in a pipelined simulation.
     */
    public void unlockAllRegisters() {
        programCounter.unlock();

        for (Register register : registers) {
            if (register != null) register.unlock();
        }

        for (Register[] array : coprocessor0Registers) {
            if (array != null) {
                for (Register register : array) {
                    if (register != null) register.unlock();
                }
            }
        }

        for (Register register : coprocessor1Registers) {
            if (register != null) register.unlock();
        }
    }

    /**
     * Enables or disabled event calls.
     * <p>
     * If this feature is disable registers will work faster, but actions won't be able to be listened.
     * <p>
     * This state won't be registered by {@link #saveState()}, but it will be copied if you use {@link #copy()}.
     *
     * @param enable whether this feature should be enabled or disabled.
     */
    public void enableEventCalls(boolean enable) {
        this.eventCallsEnabled = enable;
    }

    /**
     * Returns whether event calls are enabled.
     * <p>
     * If this feature is disable registers will work faster, but actions won't be able to be listened.
     *
     * @return whether this feature is enabled.
     * @see #enableEventCalls(boolean)
     */
    public boolean areEventCallsEnabled() {
        return eventCallsEnabled;
    }

    /**
     * Creates a copy of this register set.
     *
     * @return the copy.
     */
    public Registers copy() {
        Registers set = new Registers(validRegistersStarts, null, null, null);

        for (int i = 0; i < registers.length; i++) {
            set.registers[i] = registers[i] == null ? null : registers[i].copy(set);
        }
        for (int i = 0; i < coprocessor0Registers.length; i++) {
            set.coprocessor0Registers[i] = new Register[coprocessor0Registers[i].length];
            for (int i1 = 0; i1 < coprocessor0Registers[i].length; i1++) {
                set.coprocessor0Registers[i][i1] = coprocessor0Registers[i][i1] == null ? null : coprocessor0Registers[i][i1].copy(set);
            }
        }
        for (int i = 0; i < coprocessor1Registers.length; i++) {
            set.coprocessor1Registers[i] = coprocessor1Registers[i] == null ? null : coprocessor1Registers[i].copy(set);
        }

        set.programCounter = programCounter.copy(set);

        set.eventCallsEnabled = eventCallsEnabled;
        return set;
    }

    /**
     * Saves the current state of all {@link Register}s.
     * You can return to this state using {@link #restoreSavedState()}.
     */
    public void saveState() {
        programCounter.makeCurrentValueAsDefault();
        for (Register register : registers) {
            if (register != null) register.makeCurrentValueAsDefault();
        }
        for (Register[] registers : coprocessor0Registers) {
            for (Register register : registers) {
                if (register != null) register.makeCurrentValueAsDefault();
            }
        }
        for (Register register : coprocessor1Registers) {
            if (register != null) register.makeCurrentValueAsDefault();
        }
    }

    /**
     * Returns all {@link Register}s' states to the latest state saved using {@link #saveState()}.
     */
    public void restoreSavedState() {
        programCounter.reset();
        for (Register register : registers) {
            if (register != null) register.reset();
        }
        for (Register[] registers : coprocessor0Registers) {
            for (Register register : registers) {
                if (register != null) register.reset();
            }
        }
        for (Register register : coprocessor1Registers) {
            if (register != null) register.reset();
        }
    }

    protected void loadEssentialRegisters() {
        programCounter = new Register(this, -1, MIPS32Memory.TEXT, true, "pc");
    }
}
