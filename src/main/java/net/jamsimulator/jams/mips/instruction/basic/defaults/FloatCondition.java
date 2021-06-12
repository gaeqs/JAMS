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

package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.Jams;

public enum FloatCondition {

    AF(0b00000),
    UN(0b00001),
    EQ(0b00010),
    UEQ(0b00011),
    LT(0b00100),
    ULT(0b00101),
    LE(0b00110),
    ULE(0b00111),

    SAF(0b01000),
    SUN(0b01001),
    SEQ(0b01010),
    SUEQ(0b01011),
    SLT(0b01100),
    SULT(0b01101),
    SLE(0b01110),
    SULE(0b01111),

    OR(0b10001),
    UNE(0b10010),
    NE(0b10011),


    SOR(0b10001),
    SUNE(0b10010),
    SNE(0b10011);


    private final int code;

    FloatCondition(int code) {
        this.code = code;
    }

    public String getMnemonic() {
        return name().toLowerCase();
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return Jams.getLanguageManager().getSelected().getOrDefault("FLOAT_CONDITION_" + name());
    }
}
