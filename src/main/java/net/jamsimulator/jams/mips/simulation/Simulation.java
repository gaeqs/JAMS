/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.simulation;

import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.util.log.Console;
import net.jamsimulator.jams.gui.util.log.event.ConsoleInputEvent;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.exception.InstructionNotFoundException;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.interrupt.ExternalInterruptController;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.interrupt.MIPSInterruptException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.COP0RegistersBits;
import net.jamsimulator.jams.mips.register.COP0StatusRegister;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.event.*;
import net.jamsimulator.jams.mips.simulation.file.SimulationFiles;
import net.jamsimulator.jams.mips.simulation.random.NumberGenerators;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the execution of a set of instructions, including a memory and a register set.
 * Simulations are used to execute MIPS code.
 * <p>
 * They are based on {@link Architecture}s: the simulation should behave like the
 * architecture does on a real machine.
 * <p>
 * One step is equals to one cycle. Users should be able to execute and undo steps.
 * They should be also able to reset the simulation to their first state.
 *
 * @param <Arch> the architecture the simulation is based on.
 */
public abstract class Simulation<Arch extends Architecture> extends SimpleEventBroadcast {

    protected final Arch architecture;
    protected final InstructionSet instructionSet;
    protected final SimulationData data;

    protected final Registers registers;
    protected final Memory memory;
    protected final SimulationFiles files;
    protected final ExternalInterruptController externalInterruptController;

    protected int instructionStackBottom, kernelStackBottom;
    protected final Set<Integer> breakpoints;

    protected final NumberGenerators numberGenerators;

    protected InstructionExecution<Arch, ?>[] instructionCache;

    protected Thread thread;
    protected final Object inputLock;
    protected final Object finishedRunningLock;
    protected boolean interrupted;
    protected boolean running;
    protected boolean finished;

    protected int cycleDelay;
    protected long cycles;

    protected COP0Register badAddressRegister;
    protected COP0Register countRegister;
    protected COP0StatusRegister statusRegister;
    protected COP0Register causeRegister;
    protected COP0Register epcRegister;
    protected COP0Register eBaseRegister;
    protected COP0Register intCtlRegister;
    protected COP0Register srsCtlRegister;

    /**
     * Creates the simulation.
     *
     * @param architecture           the architecture of the simulation. This should be given by a simulation subclass.
     * @param instructionSet         the instruction used by the simulation. This set should be the same as the set used to compile the code.
     * @param registers              the registers to use on this simulation.
     * @param memory                 the memory to use in this simulation.
     * @param instructionStackBottom the address of the bottom of the instruction stack.
     * @param data                   the immutable data of this simulation.
     */
    public Simulation(Arch architecture, InstructionSet instructionSet, Registers registers, Memory memory, int instructionStackBottom, int kernelStackBottom, SimulationData data, boolean useCache) {
        this.architecture = architecture;
        this.instructionSet = instructionSet;
        this.registers = registers;
        this.memory = memory;
        this.externalInterruptController = new ExternalInterruptController();
        this.instructionStackBottom = instructionStackBottom;
        this.kernelStackBottom = kernelStackBottom;
        this.data = data;
        this.files = new SimulationFiles(this);
        this.cycleDelay = 0;

        this.breakpoints = new HashSet<>();

        this.numberGenerators = new NumberGenerators();

        // 1 Instruction = 4 Bytes.

        if (useCache) {
            instructionCache =
                    new InstructionExecution[((instructionStackBottom - memory.getFirstTextAddress()) >> 2) + 1];
            prefetch();
        }

        if (data.canCallEvents() && data.isUndoEnabled()) {
            memory.getBottomMemory().registerListeners(this, true);
            registers.registerListeners(this, true);
            files.registerListeners(this, true);
        }

        if (getConsole() != null) {
            getConsole().registerListeners(this, true);
        }

        inputLock = new Object();
        finishedRunningLock = new Object();

        running = false;
        finished = false;
        cycles = 0;

        badAddressRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(8, 0);
        countRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(9, 0);
        statusRegister = (COP0StatusRegister) registers.getCoprocessor0RegisterUnchecked(12, 0);
        causeRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(13, 0);
        epcRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(14, 0);
        eBaseRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(15, 1);
        intCtlRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(12, 1);
        srsCtlRegister = (COP0Register) registers.getCoprocessor0RegisterUnchecked(12, 2);
    }

    /**
     * Returns the {@link Architecture} this simulation is based on.
     *
     * @return the {@link Architecture}.
     */
    public Arch getArchitecture() {
        return architecture;
    }

    /**
     * Returns the {@link InstructionSet} used by this simulation. This set is used to decode instructions.
     *
     * @return the {@link InstructionSet}.
     */
    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    /**
     * Returns a instance with the immutable data of this simulation.
     *
     * @return the instance.
     */
    public SimulationData getData() {
        return data;
    }

    /**
     * Returns the {@link Registers} of this simulation.
     * <p>
     * Modifications of these registers outside the simulation won't be registered by the simulation's changes stack,
     * allowing no undo operations.
     *
     * @return the {@link Registers}.
     */
    public Registers getRegisters() {
        return registers;
    }

    /**
     * Returns the {@link Memory} of this simulation.
     * <p>
     * Modifications of the memory outside the simulation won't be registered by the simulation's changes stack,
     * allowing no undo operations.
     *
     * @return the {@link Memory}.
     */
    public Memory getMemory() {
        return memory;
    }

    /**
     * Returns the open files of this simulation. This small manager allows to open, get and close files.
     *
     * @return the {@link SimulationFiles file manager}.
     */
    public SimulationFiles getFiles() {
        return files;
    }

    /**
     * Returns the {@link Console} of this simulation.
     * This console is used to print the output of the simulation and to receive data from the user.
     *
     * @return the {@link Console}.
     */
    public Console getConsole() {
        return data.getConsole();
    }

    /**
     * Returns a unmodifiable {@link Set} with all this simulation's breakpoints.
     *
     * @return the {@link Set}.
     */
    public Set<Integer> getBreakpoints() {
        return Collections.unmodifiableSet(breakpoints);
    }

    /**
     * Returns whether the given address has a breakpoint.
     *
     * @param address the address.
     * @return whether there's a breakpoint in the given address.
     */
    public boolean hasBreakpoint(int address) {
        return breakpoints.contains(address);
    }

    /**
     * Adds a breakpoint to the given address.
     * If the given address has already a breakpoint this method returns false.
     *
     * @param address the address.
     * @return whether the operation was successful.
     */
    public boolean addBreakpoint(int address) {
        if (breakpoints.add(address)) {
            callEvent(new SimulationAddBreakpointEvent(this, address));
            return true;
        }
        return false;
    }

    /**
     * Removes a breakpoint of the given address.
     * If the given address has not a breakpoint this method returns false.
     *
     * @param address the address.
     * @return whether the operation was successful.
     */
    public boolean removeBreakpoint(int address) {
        if (breakpoints.remove(address)) {
            callEvent(new SimulationRemoveBreakpointEvent(this, address));
            return true;
        }
        return false;
    }

    /**
     * Adds or removes the breakpoint linked to the given address.
     *
     * @param address the given address.
     */
    public void toggleBreakpoint(int address) {
        if (breakpoints.contains(address)) {
            breakpoints.remove(address);
            callEvent(new SimulationRemoveBreakpointEvent(this, address));
        } else {
            breakpoints.add(address);
            callEvent(new SimulationAddBreakpointEvent(this, address));
        }
    }

    /**
     * Returns the collection containing all number generators of this simulation.
     *
     * @return the collection.
     */
    public NumberGenerators getNumberGenerators() {
        return numberGenerators;
    }

    /**
     * Returns the instruction stack bottom address.
     * This value may be modifiable if any instruction cell is modified in the {@link Memory}.
     *
     * @return the instruction stack bottom address.
     */
    public int getInstructionStackBottom() {
        return instructionStackBottom;
    }

    /**
     * Returns the instruction stack bottom address for the kernel text.
     * This value may be modifiable if any instruction cell is modified in the {@link Memory}.
     *
     * @return the instruction stack bottom address for the kernel text.
     */
    public int getKernelStackBottom() {
        return kernelStackBottom;
    }

    /**
     * Returns the delay before a cycle of this simulation in milliseconds.
     * <p>
     * If the value is 0, this simulation will run at the fastest it can.
     *
     * @return the delay in ms.
     */
    public int getCycleDelay() {
        return cycleDelay;
    }

    /**
     * Sets the delay before a cycle of this simulation in milliseconds.
     * <p>
     * If the value is 0, this simulation will run at the fastest it can.
     *
     * @param cycleDelay the delay in ms.
     */
    public void setCycleDelay(int cycleDelay) {
        this.cycleDelay = Math.max(0, cycleDelay);
    }

    /**
     * Returns the current cycle of this {@link Simulation}.
     * This is also the amount of executed cycles.
     *
     * @return the current cycle of this {@link Simulation}.
     */
    public long getCycles() {
        return cycles;
    }

    /**
     * Sets the amount oc cycles of this {@link Simulation}.
     * This field may be used when the Register {@code Count} is updated.
     *
     * @param cycles the amount of cycles.
     */
    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    /**
     * Returns whether this simulation is executing code.
     *
     * @return whether this simulatin is executing code.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Finishes the execution of this program.
     * This value can be set to false using the methods {@link  #reset()} and {@link #undoLastStep()}.
     */
    public void exit() {
        if (finished) return;
        finished = true;
        callEvent(new SimulationFinishedEvent(this));
    }

    /**
     * Request a simulation exit. This method doesn't exit the simulation automatically.
     */
    public abstract void requestExit();

    /**
     * Returns whether this simulation has finished its execution.
     *
     * @return whether this simulation has finished its execution.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Pops the next input or pauses the execution.
     * If the execution is interrupted this method returns {@code null}.
     * <p>
     * This method should be called only by the execution thread.
     */
    public String popInputOrLock() {
        Optional<String> optional = Optional.empty();
        while (optional.isEmpty()) {
            optional = data.getConsole().popInput();

            if (optional.isEmpty()) {
                try {
                    callEvent(new SimulationLockEvent(this));

                    //Flushes the console. This call is important.
                    getConsole().flush();

                    synchronized (inputLock) {
                        inputLock.wait();
                    }
                } catch (InterruptedException ex) {
                    interruptThread();
                    return null;
                }
            }
        }
        return optional.get();
    }

    /**
     * Pops the next character or pauses the execution.
     * If the execution is interrupted this method returns {@code 0}.
     * <p>
     * This method should be called only by the execution thread.
     */
    public char popCharOrLock() {
        Optional<Character> optional = Optional.empty();
        while (optional.isEmpty()) {
            optional = data.getConsole().popChar();

            if (optional.isEmpty()) {
                try {
                    callEvent(new SimulationLockEvent(this));

                    //Flushes the console. This call is important.
                    getConsole().flush();

                    synchronized (inputLock) {
                        inputLock.wait();
                    }
                } catch (InterruptedException ex) {
                    interruptThread();
                    return 0;
                }
            }
        }
        return optional.get();
    }

    /**
     * Marks the execution of this simulation as interrupted.
     * This method should be only called by the execution thread.
     * <p>
     * This will not make a MIPS32 interrupt!
     */
    public void interruptThread() {
        interrupted = true;
    }

    /**
     * Checks whether the execution thread was interrupted.
     * If true, the "interrupted" flag of the execution is marked as true.
     * This method should be only called by the execution thread.
     * <p>
     * This has nothing to do with MIPS32 interrupts!
     *
     * @return whether the execution was interrupted.
     */
    public boolean checkThreadInterrupted() {
        if (Thread.interrupted()) interrupted = true;
        return interrupted;
    }

    /**
     * Fetches the {@link InstructionExecution} located at the given address.
     * <p>
     * This method may return {@code null} if the instruction cannot be decoded.
     *
     * @param pc the address to fetch.
     * @return the {@link InstructionExecution} or null.
     * @throws IllegalArgumentException  if the given address is not aligned to words.
     * @throws IndexOutOfBoundsException if the address if out of bounds.
     */
    public InstructionExecution<? super Arch, ?> fetch(int pc) {
        InstructionExecution<Arch, ?> cached;
        boolean useCache = instructionCache != null && Integer.compareUnsigned(pc, instructionStackBottom) <= 0;
        if (useCache) {
            cached = instructionCache[(pc - memory.getFirstTextAddress()) >> 2];
            if (cached != null) return cached;
        }

        int data = memory.getWord(pc);

        Optional<? extends BasicInstruction<?>> optional = instructionSet.getInstructionByInstructionCode(data);
        if (optional.isEmpty()) return null;
        BasicInstruction<?> instruction = optional.get();
        AssembledInstruction assembled = instruction.assembleFromCode(data);
        cached = assembled.getBasicOrigin().generateExecution(this, assembled, pc).orElse(null);


        if (useCache) {
            instructionCache[(pc - memory.getFirstTextAddress()) >> 2] = cached;
        }
        return cached;
    }


    /**
     * Increases the cycle count by one.
     * This also modifies the register {@code Count} if enabled.
     */
    public void addCycleCount() {
        cycles++;
        if (countRegister != null && (causeRegister == null || !causeRegister.getBit(COP0RegistersBits.CAUSE_DC))) {
            countRegister.setValue(countRegister.getValue() + 1);
        }
    }

    /**
     * Returns whether MIPS interrupts are enabled.
     *
     * @return whether MIPS interrupts are enabled.
     */
    public boolean areMIPSInterruptsEnabled() {
        // This method checks fields ERL, EXL and IE of the status registers.
        // Interrupts are enabled only if ERL and EXL are 0 and IE is 1.
        return statusRegister.getSection(COP0RegistersBits.STATUS_IE, 3) == 1;
    }

    /**
     * Sets whether MIPS interrupts are enabled.
     * This method only sets the {@code IE} field of the registert {@code Status}.
     *
     * @param enable whether interrupts are enabled.
     */
    public void enableMIPSInterrupts(boolean enable) {
        statusRegister.modifyBits(enable ? 1 : 0, COP0RegistersBits.STATUS_IE, 1);
    }

    /**
     * Returns whether this simulation is in kernel mode.
     *
     * @return whether this simulation is in kernel mode.
     */
    public boolean isKernelMode() {
        return !statusRegister.getBit(COP0RegistersBits.STATUS_UM);
    }

    /**
     * Returns whether this simulation is in the exception level.
     *
     * @return whether this simulation is in the exception level.
     */
    public boolean isInExceptionLevel() {
        return statusRegister.getBit(COP0RegistersBits.STATUS_EXL);
    }

    /**
     * Returns the level of the interrupt currently being executed.
     * <p>
     * If this value is 0, the simulation is not executing any interrupt.
     *
     * @return the level of the interrupt currently being executed.
     */
    public int getIPLevel() {
        return statusRegister.getSection(COP0RegistersBits.STATUS_IPL, 6);
    }

    public synchronized void requestHardwareInterrupt(int level) {
        externalInterruptController.addRequest(level);
    }

    public synchronized void requestSoftwareInterrupt(MIPSInterruptException interrupt) {
        externalInterruptController.addSoftwareRequest(interrupt);
        causeRegister.modifyBits(1, COP0RegistersBits.CAUSE_IP, 1);
    }

    protected boolean arePendingInterrupts() {
        return externalInterruptController.isRequestingInterrupts(this);
    }

    protected void invokeInterrupt(InterruptCause type, MIPSInterruptException exception,
                                   boolean delaySlot, int pc) {
        boolean exl = isInExceptionLevel();
        int level = causeRegister.getSection(COP0RegistersBits.CAUSE_RIPL, 6);

        if (!exl) {
            epcRegister.setValue(delaySlot ? pc - 4 : pc);
            causeRegister.modifyBits(delaySlot ? 1 : 0, COP0RegistersBits.CAUSE_BD, 1);
        }

        causeRegister.modifyBits(type.getValue(), COP0RegistersBits.CAUSE_EX_CODE, 5);
        statusRegister.modifyBits(1, COP0RegistersBits.STATUS_EXL, 1);
        statusRegister.modifyBits(level, COP0RegistersBits.STATUS_IPL, 6);

        int jump = generateExceptionVectorJump(exl, type, level);
        registers.getProgramCounter().setValue(jump);

        if (exception instanceof MIPSAddressException) {
            badAddressRegister.modifyBits(((MIPSAddressException) exception).getBadAddress(), 0, 32);
        }

        if (memory.getWord(jump, false, true, true) == 0) {
            finished = true;
            if (getConsole() != null) {
                getConsole().println();
                getConsole().printErrorLn("Execution finished. Runtime exception at 0x"
                        + StringUtils.addZeros(Integer.toHexString(pc), 8)
                        + ": Code " + type.getValue() + " (" + type + ")");
                getConsole().println();
            }
            callEvent(new SimulationFinishedEvent(this));
        }
    }

    /**
     * Stops the execution of the simulation.
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * Returns the simulation to its first state.
     * <p>
     * This method depends on {@link Registers#restoreSavedState()} and {@link Memory#restoreSavedState()}.
     * Any invocation to {@link Registers#saveState()} and {@link Memory#saveState()} on this simulation's
     * {@link Memory} and {@link Registers} may result on unexpected results.
     */
    public void reset() {
        stop();
        waitForExecutionFinish();

        registers.restoreSavedState();
        memory.restoreSavedState();
        finished = false;
        cycles = 0;
        externalInterruptController.reset();

        callEvent(new SimulationResetEvent(this));
    }

    /**
     * Waits till the current execution is finished.
     */
    public void waitForExecutionFinish() {
        synchronized (finishedRunningLock) {
            if (running) {
                //Wait till cycle restoration.
                try {
                    finishedRunningLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes the next step of this simulation.
     *
     * @throws InstructionNotFoundException when an instruction couldn't be decoded or when the bottom of the instruction stack is reached.
     */
    public void nextStep() {
        if (finished || running) return;
        running = true;
        interrupted = false;

        memory.enableEventCalls(data.canCallEvents());
        registers.enableEventCalls(data.canCallEvents());

        thread = new Thread(() -> {
            try {
                runStep(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            manageSimulationFinish();
        });
        callEvent(new SimulationStartEvent(this));
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }


    /**
     * Executes steps until the bottom of the instruction stack is reached.
     *
     * @throws InstructionNotFoundException when an instruction couldn't be decoded.
     */
    public void executeAll() {
        if (finished || running) return;
        running = true;
        interrupted = false;

        memory.enableEventCalls(data.canCallEvents());
        registers.enableEventCalls(data.canCallEvents());

        thread = new Thread(() -> {

            long cyclesStart = cycles;
            long start = System.nanoTime();

            try {
                runStep(true);
                while (!finished && !checkThreadInterrupted()) {
                    velocitySleep();
                    if (!checkThreadInterrupted()) {
                        runStep(false);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (getConsole() != null) {
                long millis = (System.nanoTime() - start) / 1000000;
                getConsole().println();
                getConsole().printInfoLn(cycles - cyclesStart + " cycles executed in " + millis + " millis.");

                int performance = (int) ((cycles - cyclesStart) / (((double) millis) / 1000));
                getConsole().printInfoLn(performance + " cycle/s");
                getConsole().println();
            }

            manageSimulationFinish();
        });
        callEvent(new SimulationStartEvent(this));
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void manageSimulationFinish() {
        synchronized (finishedRunningLock) {
            running = false;
            memory.enableEventCalls(true);
            registers.enableEventCalls(true);
            finishedRunningLock.notifyAll();
            callEvent(new SimulationStopEvent(this));
            if (getConsole() != null) {
                getConsole().flush();
            }
        }
    }

    /**
     * Resets all the caches working with this simulation.
     * This also removes all cycle changes: the undo feature won't undo a reset!
     * <p>
     * This method won't work if the simulation is running!
     *
     * @return whether the operation was successful.
     */
    public boolean resetCaches() {
        if (running) return false;
        Optional<Memory> current = Optional.of(memory);
        while (current.isPresent()) {
            if (current.get() instanceof Cache) {
                ((Cache) current.get()).resetCache();
            }
            current = current.get().getNextLevelMemory();
        }
        callEvent(new SimulationCachesResetEvent(this));
        return true;
    }

    /**
     * Runs the given code synchronized to this simulation.
     * <p>
     * This allows to modify registers and memory safely.
     *
     * @param runnable the code to run.
     */
    public synchronized void runSynchronized(Runnable runnable) {
        runnable.run();
    }

    /**
     * Sleeps the simulation the amount of time specified by the {@link #cycleDelay} variable.
     */
    protected void velocitySleep() {
        if (cycleDelay > 0) {
            try {
                Thread.sleep(cycleDelay);
            } catch (InterruptedException e) {
                interruptThread();
            }
        }
    }

    protected void prefetch() {
        //instructionCache[(pc - memory.getFirstTextAddress()) >> 2];
        for (int i = 0; i < instructionCache.length; i++) {
            int address = (i << 2) + memory.getFirstTextAddress();
            int data = memory.getWord(address);
            var instruction =
                    instructionSet.getInstructionByInstructionCode(data).orElseThrow();
            var assembled = instruction.assembleFromCode(data);
            var execution = assembled.getBasicOrigin()
                    .generateExecution(this, assembled, address).orElseThrow();

            instructionCache[i] = execution;
        }

    }

    protected int generateExceptionVectorJump(boolean exceptionLevel, InterruptCause cause, int level) {
        final int BASE_CONSTANT = 0x80000000;
        final int BASE_MASK = 0x3fffffff;

        int base = eBaseRegister.getValue();
        int offset = exceptionLevel ? 0x180 : switch (cause) {
            case CACHE_ERROR -> 0x100;
            case INTERRUPT -> {
                int vs = intCtlRegister.getSection(COP0RegistersBits.INT_CTL_VS, 5) << 5;
                yield 0x200 + level * vs;
            }
            default -> 0x180;
        };

        return base + offset & BASE_MASK | BASE_CONSTANT;
    }

    /**
     * Executes the next step of the simulation.
     * <p>
     * These method must be synchronized!
     *
     * @param first whether this is the first step made by this execution.
     */
    protected abstract void runStep(boolean first);

    protected abstract void manageInterrupts(InstructionExecution<?, ?> execution);

    /**
     * Undoes the last step made by this simulation.
     * This method won't do nothing if no steps were made.
     * <p>
     * If this simulation was execution all instructions and this method is used,
     * the simulation will stop.
     *
     * @return whether a step was undone.
     */
    public abstract boolean undoLastStep();

    //TODO this should be reworked.
    @Listener
    private void onMemoryChange(MemoryByteSetEvent.After event) {
        int address = event.getAddress() >> 2 << 2;

        if (address >= memory.getFirstTextAddress() && address <= instructionStackBottom) {
            instructionCache[(address - memory.getFirstTextAddress()) >> 2] = null;
        }

        var memorySection = event.getMemorySection().orElse(null);
        if (memorySection != null && memorySection.getName().equals("Text") && instructionStackBottom < event.getAddress()) {
            instructionStackBottom = address;

            InstructionExecution<Arch, ?>[] array =
                    new InstructionExecution[((instructionStackBottom - memory.getFirstTextAddress()) >> 2) + 1];
            System.arraycopy(instructionCache, 0, array, 0, instructionCache.length);
            instructionCache = array;
        }
    }

    @Listener
    private void onMemoryChange(MemoryWordSetEvent.After event) {
        int address = event.getAddress();

        if (address >= memory.getFirstTextAddress() && address <= instructionStackBottom) {
            instructionCache[(address - memory.getFirstTextAddress()) >> 2] = null;
        }
        var memorySection = event.getMemorySection().orElse(null);
        if (memorySection != null && memorySection.getName().equals("Text") && instructionStackBottom < event.getAddress()) {
            instructionStackBottom = address;

            InstructionExecution<Arch, ?>[] array =
                    new InstructionExecution[((instructionStackBottom - memory.getFirstTextAddress()) >> 2) + 1];
            System.arraycopy(instructionCache, 0, array, 0, instructionCache.length);
            instructionCache = array;
        }
    }

    @Listener
    private void onInput(ConsoleInputEvent.After event) {
        callEvent(new SimulationUnlockEvent(this));
        synchronized (inputLock) {
            inputLock.notifyAll();
        }
    }

}
