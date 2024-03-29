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

public enum InterruptCause {

    INTERRUPT("Int", 0x00),
    TLB_MODIFICATION_EXCEPTION("Mod", 0x01),
    TLB_LOAD_EXCEPTION("TLBL", 0x02),
    TLB_STORE_EXCEPTION("TLBS", 0x03),
    ADDRESS_LOAD_EXCEPTION("AdEL", 0x04),
    ADDRESS_STORE_EXCEPTION("AdES", 0x05),
    INSTRUCTION_FETCH_BUS_EXCEPTION("IBE", 0x06),
    DATA_BUS_EXCEPTION("DBE", 0x07),
    SYSCALL_EXCEPTION("Sys", 0x08),
    BREAKPOINT_EXCEPTION("Bp", 0x09),
    RESERVED_INSTRUCTION_EXCEPTION("RI", 0x0A),
    COPROCESSOR_UNUSABLE_EXCEPTION("CpU", 0x0B),
    ARITHMETIC_OVERFLOW_EXCEPTION("Ov", 0x0C),
    TRAP_EXCEPTION("Tr", 0x0D),
    MSA_FLOATING_POINT_EXCEPTION("MSAFPE", 0x0E),
    FLOATING_POINT_EXCEPTION("MSAFPE", 0x0F),
    CUSTOM_INTERRUPT_1("CI1", 0x10),
    CUSTOM_INTERRUPT_2("CI2", 0x11),
    COPROCESSOR_2_EXCEPTION("C2E", 0x12),
    TLB_READ_INHIBIT_EXCEPTION("TLBRI", 0x13),
    TLB_EXECUTION_INHIBIT_EXCEPTION("TLBXI", 0x14),
    MSA_DISABLED_EXCEPTION("MSADis", 0x15),
    MDMX_UNUSABLE_EXCEPTION("MDMX", 0X16),
    REFERENCE_TO_WATCH("WATCH", 0X17),
    MACHINE_CHECK("MCheck", 0x18),
    THREAD_EXCEPTION("Thread", 0x19),
    DPS_DISABLED_EXCEPTION("DSPDis", 0x1A),
    VIRTUALIZED_GUEST_EXCEPTION("GE", 0x1B),
    RESERVED_1("R1", 0x1C),
    RESERVED_2("R2", 0x1D),
    CACHE_ERROR("CacheErr", 0x1E),
    RESERVED_3("R3", 0x1F);

    private final String mnemonic;
    private final int value;

    InterruptCause(String mnemonic, int value) {
        this.mnemonic = mnemonic;
        this.value = value;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public int getValue() {
        return value;
    }
}
