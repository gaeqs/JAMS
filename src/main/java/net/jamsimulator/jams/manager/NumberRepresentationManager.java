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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.utils.NumberRepresentation;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * This singleton stores all {@link NumberRepresentation}s that JAMs may use.
 * <p>
 * To register an {@link NumberRepresentation} use {@link Manager#add(Labeled)}}.
 * To unregister an {@link NumberRepresentation} use {@link #remove(Object)}.
 * A {@link NumberRepresentation}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public final class NumberRepresentationManager extends Manager<NumberRepresentation> {

    public static final NumberRepresentation HEXADECIMAL = new NumberRepresentation("HEXADECIMAL",
            false, false,
            (o1, o2) -> "0x" + StringUtils.addZeros(Integer.toHexString(o1), 8));
    public static final NumberRepresentation DECIMAL = new NumberRepresentation("DECIMAL",
            false, false,
            (o1, o2) -> String.valueOf(o1));
    public static final NumberRepresentation OCTAL = new NumberRepresentation("OCTAL",
            false, false,
            (o1, o2) -> "0" + Integer.toOctalString(o1));
    public static final NumberRepresentation BINARY = new NumberRepresentation("BINARY",
            false, false,
            (o1, o2) -> "0b" + StringUtils.addZeros(Integer.toBinaryString(o1), 32));
    public static final NumberRepresentation LONG = new NumberRepresentation("LONG",
            true, false,
            (o1, o2) -> String.valueOf(NumericUtils.intsToLong(o1, o2)));
    public static final NumberRepresentation FLOAT = new NumberRepresentation("FLOAT",
            false, false,
            (o1, o2) -> String.valueOf(Float.intBitsToFloat(o1)));
    public static final NumberRepresentation DOUBLE = new NumberRepresentation("DOUBLE",
            true, false,
            (o1, o2) -> String.valueOf(NumericUtils.intsToDouble(o1, o2)));
    public static final NumberRepresentation CHAR = new NumberRepresentation("CHAR",
            false, false,
            (o1, o2) -> {
                char[] array = new char[4];
                for (int i = 0; i < 4; i++) {
                    array[i] = (char) ((o1 >> i * 8) & 0xFF);
                }
                return new String(array);
            });
    public static final NumberRepresentation RGB = new NumberRepresentation("RGB",
            false, true,
            (o1, o2) -> getRGBAsString(o1));
    public static final NumberRepresentation RGBA = new NumberRepresentation("RGBA",
            false, true,
            (o1, o2) -> getRGBAAsString(o1));
    public static final NumberRepresentation ENGLISH = new NumberRepresentation("ENGLISH",
            false, false,
            (o1, o2) -> NumericUtils.toEnglish(o1));
    public static final NumberRepresentation ROMAN = new NumberRepresentation("ROMAN",
            false, false,
            (o1, o2) -> NumericUtils.toRoman(o1));

    public static final NumberRepresentationManager INSTANCE = new NumberRepresentationManager();

    private NumberRepresentationManager() {
        super(NumberRepresentation.class);
    }

    private static String getRGBAsString(int value) {
        String val = StringUtils.addZeros(Integer.toHexString(value), 6);
        if (val.length() > 6) val = val.substring(val.length() - 6);
        return "#" + val;
    }

    private static String getRGBAAsString(int value) {
        return "#" + StringUtils.addZeros(Integer.toHexString(value), 8);
    }

    @Override
    protected void loadDefaultElements() {
        add(HEXADECIMAL);
        add(DECIMAL);
        add(OCTAL);
        add(BINARY);
        add(LONG);
        add(FLOAT);
        add(DOUBLE);
        add(CHAR);
        add(RGB);
        add(RGBA);
        add(ENGLISH);
        add(ROMAN);
    }
}
