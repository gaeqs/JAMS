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

package net.jamsimulator.jams.mips.register.builder;

import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Registers;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a MIPS32 registers builder. Use this to create {@link MIPS32Registers}.
 */
public class MIPS32RegistersBuilder extends RegistersBuilder {

    public static final String NAME = "MIPS32";
    private static final Set<String> NAMES = new HashSet<>();
    private static final Set<String> GENERAL_NAMES = new HashSet<>();
    private static final Set<String> COP0_NAMES = new HashSet<>();
    private static final Set<String> COP1_NAMES = new HashSet<>();

    static {
        NAMES.add("zero");
        NAMES.add("at");
        NAMES.add("v0");
        NAMES.add("v1");
        for (int i = 0; i < 4; i++)
            NAMES.add("a" + i);
        for (int i = 0; i < 8; i++)
            NAMES.add("t" + i);
        for (int i = 0; i < 8; i++)
            NAMES.add("s" + i);
        NAMES.add("t8");
        NAMES.add("t9");
        NAMES.add("k0");
        NAMES.add("k1");
        NAMES.add("gp");
        NAMES.add("sp");
        NAMES.add("fp");
        NAMES.add("ra");
        GENERAL_NAMES.addAll(NAMES);

        for (int i = 0; i < 32; i++) {
            NAMES.add("f" + i);
            NAMES.add(String.valueOf(i));
            COP1_NAMES.add("f" + i);
            COP1_NAMES.add(String.valueOf(i));
            GENERAL_NAMES.add(String.valueOf(i));
        }

        NAMES.add("8");
        NAMES.add("12");
        NAMES.add("13");
        NAMES.add("14");
        COP0_NAMES.add("8");
        COP0_NAMES.add("12");
        COP0_NAMES.add("13");
        COP0_NAMES.add("14");
    }

    MIPS32RegistersBuilder(ResourceProvider provider) {
        super(provider, NAME, NAMES, GENERAL_NAMES, COP0_NAMES, COP1_NAMES,
                MIPS32Registers.VALID_REGISTERS_START);
    }

    @Override
    public Registers createRegisters() {
        return new MIPS32Registers();
    }
}
