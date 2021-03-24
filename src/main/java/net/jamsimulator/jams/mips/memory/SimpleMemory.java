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

package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.memory.event.*;
import net.jamsimulator.jams.utils.StringUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a simple memory. A simple memory is formed by several {@link MemorySection},
 * which are responsible of storing data in their {@link MemoryCell}s.
 * <p>
 * Once created you cannot add or remove {@link MemorySection}s.
 * <p>
 * This memory should be located at the last hierarchy level.
 *
 * @see EventBroadcast
 * @see MemorySection
 * @see MemoryCell
 */
public class SimpleMemory extends SimpleEventBroadcast implements Memory {


    protected MemorySection[] sections;
    protected int[] firstAddresses;
    protected MemorySection[] savedSections;

    protected boolean bigEndian;
    protected boolean savedEndian;

    protected final int firstTextAddress, firstDataAddress, firstKernelTextAddress, firstKernelDataAddress, firstExternalAddress;
    protected int nextDataAddress;
    protected int savedNextDataAddress;

    protected boolean eventCallsEnabled;

    /**
     * Creates a simple memory using a list of {@link MemorySection}s and a boolean representing whether
     * this memory is bigEndian.
     *
     * @param sections  the {@link MemorySection}s.
     * @param bigEndian whether this memory is big endian.
     */
    public SimpleMemory(Collection<MemorySection> sections, boolean bigEndian, int firstTextAddress, int firstDataAddress,
                        int firstKernelTextAddress, int firstKernelDataAddress, int firstExternalAddress) {
        Validate.notNull(sections, "There must be at least one memory section!");
        Validate.isTrue(!sections.isEmpty(), "There must be at least one memory section!");
        this.sections = new MemorySection[sections.size()];
        this.bigEndian = bigEndian;
        this.savedEndian = bigEndian;

        this.firstTextAddress = firstTextAddress;
        this.firstDataAddress = firstDataAddress;
        this.firstKernelTextAddress = firstKernelTextAddress;
        this.firstKernelDataAddress = firstKernelDataAddress;
        this.firstExternalAddress = firstExternalAddress;

        this.nextDataAddress = firstDataAddress;
        this.savedNextDataAddress = firstDataAddress;

        int i = 0;
        for (MemorySection section : sections) {
            this.sections[i++] = section;
        }
        Arrays.sort(this.sections, (o1, o2) -> Integer.compareUnsigned(o1.getFirstAddress(), o2.getFirstAddress()));

        this.firstAddresses = new int[this.sections.length];
        for (i = 0; i < this.sections.length; i++) {
            firstAddresses[i] = this.sections[i].getFirstAddress();
        }

        this.eventCallsEnabled = true;
    }

    /**
     * Creates a simple memory using an array of {@link MemorySection}s and a boolean representing whether
     * this memory is bigEndian.
     *
     * @param sections  the {@link MemorySection}s.
     * @param bigEndian whether this memory is big endian.
     */
    public SimpleMemory(boolean bigEndian, int firstTextAddress, int firstDataAddress, int firstKernelTextAddress,
                        int firstKernelDataAddress, int firstExternalAddress, MemorySection... sections) {
        Validate.isTrue(sections.length > 0, "There must be at least one memory section!");
        this.sections = new MemorySection[sections.length];
        this.bigEndian = bigEndian;
        this.savedEndian = bigEndian;

        this.firstTextAddress = firstTextAddress;
        this.firstDataAddress = firstDataAddress;
        this.firstKernelTextAddress = firstKernelTextAddress;
        this.firstKernelDataAddress = firstKernelDataAddress;
        this.firstExternalAddress = firstExternalAddress;

        this.nextDataAddress = firstDataAddress;
        this.savedNextDataAddress = firstDataAddress;

        System.arraycopy(sections, 0, this.sections, 0, sections.length);
        Arrays.sort(this.sections, (o1, o2) -> Integer.compareUnsigned(o1.getFirstAddress(), o2.getFirstAddress()));

        this.firstAddresses = new int[this.sections.length];
        for (int i = 0; i < this.sections.length; i++) {
            firstAddresses[i] = this.sections[i].getFirstAddress();
        }

        this.eventCallsEnabled = true;
    }

    protected SimpleMemory(MemorySection[] sections, boolean bigEndian, int firstTextAddress,
                           int firstDataAddress, int firstKernelTextAddress, int firstKernelDataAddress, int firstExternalAddress) {
        Validate.notNull(sections, "There must be at least one memory section!");
        Validate.isTrue(sections.length != 0, "There must be at least one memory section!");
        this.sections = sections;
        this.bigEndian = bigEndian;
        this.savedEndian = bigEndian;

        this.firstTextAddress = firstTextAddress;
        this.firstDataAddress = firstDataAddress;
        this.firstKernelTextAddress = firstKernelTextAddress;
        this.firstKernelDataAddress = firstKernelDataAddress;
        this.firstExternalAddress = firstExternalAddress;

        this.nextDataAddress = firstDataAddress;
        this.savedNextDataAddress = firstDataAddress;

        this.eventCallsEnabled = true;

        Arrays.sort(this.sections, (o1, o2) -> Integer.compareUnsigned(o1.getFirstAddress(), o2.getFirstAddress()));

        this.firstAddresses = new int[this.sections.length];
        for (int i = 0; i < this.sections.length; i++) {
            firstAddresses[i] = this.sections[i].getFirstAddress();
        }
    }

    @Override
    public boolean isBigEndian() {
        return bigEndian;
    }

    @Override
    public void setBigEndian(boolean bigEndian) {
        if (!eventCallsEnabled) {
            this.bigEndian = bigEndian;
            return;
        }
        if (this.bigEndian == bigEndian) return;
        MemoryEndiannessChange.Before before = callEvent(new MemoryEndiannessChange.Before(this, bigEndian));
        if (before.isCancelled()) return;
        if (this.bigEndian == before.isNewEndiannessBigEndian()) return;

        this.bigEndian = before.isNewEndiannessBigEndian();

        callEvent(new MemoryEndiannessChange.After(this, this.bigEndian));
    }

    @Override
    public byte getByte(int address) {
        return getByte(address, true, false, true);
    }

    @Override
    public void setByte(int address, byte b) {
        setByte(address, b, true, false, true);
    }

    @Override
    public int getWord(int address) {
        return getWord(address, true, false, true);
    }

    @Override
    public void setWord(int address, int word) {
        setWord(address, word, true, false, true);
    }

    @Override
    public void setWord(int address, int word, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x2) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_STORE_EXCEPTION, address);
        if (!eventCallsEnabled || !callEvents) {
            getSectionOrThrowException(address).setWord(address, word, bigEndian);
            return;
        }
        //Invokes the before event.
        MemoryWordSetEvent.Before before = callEvent(new MemoryWordSetEvent.Before(this, address, word));
        if (before.isCancelled()) return;

        //Refresh data.
        address = before.getAddress();
        word = before.getValue();

        //Gets the section and sets the word.
        MemorySection section = getSectionOrThrowException(address);
        int old = section.setWord(address, word, bigEndian);

        //Invokes the after event.
        callEvent(new MemoryWordSetEvent.After(this, section, address, word, old));
    }

    @Override
    public void setByte(int address, byte b, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if (!eventCallsEnabled || !callEvents) {
            getSectionOrThrowException(address).setByte(address, b);
            return;
        }
        //Invokes the before event.
        MemoryByteSetEvent.Before before = callEvent(new MemoryByteSetEvent.Before(this, address, b));
        if (before.isCancelled()) return;

        //Refresh data.
        address = before.getAddress();
        b = before.getValue();

        //Gets the section and sets the byte.
        MemorySection section = getSectionOrThrowException(address);
        byte old = section.setByte(address, b);

        //Invokes the after event.
        callEvent(new MemoryByteSetEvent.After(this, section, address, b, old));
    }

    @Override
    public int getWord(int address, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x2) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        if (!eventCallsEnabled || !callEvents) {
            return getSectionOrThrowException(address).getWord(address, bigEndian);
        }
        //Invokes the before event.
        MemoryWordGetEvent.Before before = callEvent(new MemoryWordGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the word.
        MemorySection section = getSectionOrThrowException(address);
        int word = section.getWord(address, bigEndian);

        //Invokes the after event.
        return callEvent(new MemoryWordGetEvent.After(this, section, address, word)).getValue();
    }

    @Override
    public byte getByte(int address, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if (!eventCallsEnabled || !callEvents) {
            return getSectionOrThrowException(address).getByte(address);
        }
        //Invokes the before event.
        MemoryByteGetEvent.Before before = callEvent(new MemoryByteGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the byte.
        MemorySection section = getSectionOrThrowException(address);
        byte b = section.getByte(address);

        //Invokes the after event.
        return callEvent(new MemoryByteGetEvent.After(this, section, address, b)).getValue();
    }

    @Override
    public int getFirstTextAddress() {
        return firstTextAddress;
    }

    @Override
    public int getFirstDataAddress() {
        return firstDataAddress;
    }

    @Override
    public int getNextDataAddress() {
        return nextDataAddress;
    }

    @Override
    public void setNextDataAddress(int nextDataAddress) {
        this.nextDataAddress = nextDataAddress;
    }

    @Override
    public int getFirstKernelTextAddress() {
        return firstKernelTextAddress;
    }

    @Override
    public int getFirstKernelDataAddress() {
        return firstKernelDataAddress;
    }

    @Override
    public int getFirstExternalAddress() {
        return firstExternalAddress;
    }

    @Override
    public Optional<Memory> getNextLevelMemory() {
        return Optional.empty();
    }

    @Override
    public int allocateMemory(int length) {
        //Align
        int first = nextDataAddress;
        int mod = first % 4;
        if (mod > 0) {
            first += 4 - mod;
        }

        MemoryAllocateMemoryEvent.Before before =
                callEvent(new MemoryAllocateMemoryEvent.Before(this, nextDataAddress, first, length));
        if (before.isCancelled()) return 0;

        length = before.getLength();

        int old = nextDataAddress;
        nextDataAddress = first + length;

        callEvent(new MemoryAllocateMemoryEvent.After(this, old, first, length));

        return first;
    }

    @Override
    public void enableEventCalls(boolean enable) {
        this.eventCallsEnabled = enable;
    }

    @Override
    public boolean areEventCallsEnabled() {
        return eventCallsEnabled;
    }

    @Override
    public Memory copy() {

        MemorySection[] sections = new MemorySection[this.sections.length];
        for (int i = 0; i < this.sections.length; i++) {
            sections[i] = this.sections[i].copy();
        }

        SimpleMemory memory = new SimpleMemory(sections, bigEndian, firstTextAddress, firstDataAddress, firstKernelTextAddress, firstKernelDataAddress, firstExternalAddress);
        memory.savedNextDataAddress = nextDataAddress;
        memory.nextDataAddress = nextDataAddress;
        memory.eventCallsEnabled = eventCallsEnabled;
        return memory;
    }

    @Override
    public void saveState() {
        savedSections = new MemorySection[sections.length];
        for (int i = 0; i < sections.length; i++) {
            savedSections[i] = sections[i].copy();
        }
        savedEndian = bigEndian;
        savedNextDataAddress = nextDataAddress;
    }

    @Override
    public void restoreSavedState() {
        if (savedSections == null) {
            for (MemorySection section : sections) {
                section.wipe();
            }
        } else {
            for (int i = 0; i < sections.length; i++) {
                sections[i] = savedSections[i].copy();
            }
        }
        bigEndian = savedEndian;
        nextDataAddress = savedNextDataAddress;
    }

    @Override
    public Set<MemorySection> getMemorySections() {
        List<MemorySection> list = Arrays.asList(sections);
        return Collections.unmodifiableSet(new HashSet<>(list));
    }

    @Override
    public String getMemorySectionName(int address) {
        return getSectionOrThrowException(address).getName();
    }

    @Override
    public Optional<MemorySection> getMemorySection(String name) {
        if (name == null || name.isEmpty()) return Optional.empty();
        for (MemorySection section : sections) {
            if (section.getName().equals(name)) return Optional.of(section);
        }
        return Optional.empty();
    }

    @Override
    public MemorySection getMemorySection(int address) {
        return getSectionOrThrowException(address);
    }

    @Override
    public boolean isDirectionAffectedByCache(int address) {
        return true;
    }


    private MemorySection getSectionOrThrowException(int address) {
        //Optimized for loop.
        for (int i = 0, length = firstAddresses.length; i < length; i++) {
            if (Integer.compareUnsigned(firstAddresses[i], address) > 0)
                return sections[i - 1];
        }

        var last = sections[sections.length - 1];
        if (last.isInside(address)) return last;

        throw new IndexOutOfBoundsException("Memory section not found for address 0x"
                + StringUtils.addZeros(Integer.toHexString(address), 8) + ".");
    }
}
