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

import java.util.Collection;

public class COP0StatusRegister extends COP0Register {

    public COP0StatusRegister(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, String... names) {
        super(registers, identifier, selection, softwareWriteMask, cop0Name, names);
    }

    public COP0StatusRegister(Registers registers, int identifier, int selection, int softwareWriteMask, String cop0Name, Collection<String> names) {
        super(registers, identifier, selection, softwareWriteMask, cop0Name, names);
    }

    public COP0StatusRegister(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, String... names) {
        super(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
    }

    public COP0StatusRegister(Registers registers, int identifier, int selection, int value, int softwareWriteMask, String cop0Name, Collection<String> names) {
        super(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
    }


    @Override
    protected void setValue0(int value) {
        super.setValue0(value);

        boolean userMode = getSection(COP0RegistersBits.STATUS_EXL, 2) == 0;
        if (getBit(COP0RegistersBits.STATUS_UM) != userMode) {
            int mask = 1 << COP0RegistersBits.STATUS_UM;
            this.value &= ~mask;
            if (userMode) {
                this.value |= mask;
            }
        }
    }

    @Override
    public COP0Register copy(Registers registers) {
        COP0StatusRegister register = new COP0StatusRegister(registers, identifier, selection, value, softwareWriteMask, cop0Name, names);
        register.defaultValue = defaultValue;
        return register;
    }
}
