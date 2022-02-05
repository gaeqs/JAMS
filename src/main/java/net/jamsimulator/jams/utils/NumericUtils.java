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

package net.jamsimulator.jams.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Optional;

public class NumericUtils {

    private static final String[] ENGLISH_TEN_NAMES = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] ENGLISH_NUMBER_NAMES = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private final static int[] ROMAN_NUMBERS_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    private final static String[] ROMAN_NUMBERS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};


    public static boolean isInteger(String string) {
        try {
            decodeInteger(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isLong(String string) {
        try {
            Long.decode(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isShort(String string) {
        try {
            Short.parseShort(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isByte(String string) {
        try {
            Byte.parseByte(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns whether the given number can be expressed as 2^n.
     *
     * @param number the number.
     * @return whether the  number can be expressed as 2^n.
     */
    public static boolean is2Elev(int number) {
        if (number <= 0) return false;

        while (number > 0) {
            if ((number & 1) == 1) {
                return (number >> 1) == 0;
            }
            number >>= 1;
        }

        return false;
    }

    /**
     * Executes the mathematical operation log2(n).
     * The result will be exact only for 2^n numbers.
     *
     * @param n the number.
     * @return the result.
     */
    public static int log2(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    /**
     * Clamps the given value.
     *
     * @param min the minimum value.
     * @param val the value.
     * @param max the maximum value.
     * @return the clamped value.
     */
    public static double clamp(double min, double val, double max) {
        if (val < min) {
            return min;
        } else {
            return Math.min(val, max);
        }
    }

    public static int decodeInteger(String string) {
        string = string.trim();
        char c = '+';
        if (string.startsWith("+"))
            string = string.substring(1);
        else if (string.startsWith("-")) {
            c = '-';
            string = string.substring(1);
        }

        int radix = 10;
        int substring = 0;
        if (string.startsWith("0x") || string.startsWith("0X") || string.startsWith("#")) {
            radix = 16;
            substring = 2;
        }
        if (string.startsWith("0o") || string.startsWith("0O")) {
            radix = 8;
            substring = 2;
        }
        if (string.startsWith("0b") || string.startsWith("0B")) {
            radix = 2;
            substring = 2;
        }

        return new BigInteger(c + string.substring(substring), radix).intValue();
    }

    public static Optional<Integer> decodeIntegerSafe(String string) {
        try {
            return Optional.of(decodeInteger(string));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static double intsToDouble(int low, int high) {
        return Double.longBitsToDouble((((long) high) << 32) + low);
    }

    public static int[] doubleToInts(double d) {
        int[] array = new int[2];
        long l = Double.doubleToLongBits(d);
        array[0] = (int) l;
        array[1] = (int) (l >> 32);
        return array;
    }

    public static int[] longToInts(long l) {
        int[] array = new int[2];
        array[0] = (int) l;
        array[1] = (int) (l >> 32);
        return array;
    }

    public static long intsToLong(int low, int high) {
        return ((long) high << 32) + low;
    }

    private static String toEnglishLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = ENGLISH_NUMBER_NAMES[number % 100];
            number /= 100;
        } else {
            soFar = ENGLISH_NUMBER_NAMES[number % 10];
            number /= 10;

            soFar = ENGLISH_TEN_NAMES[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return ENGLISH_NUMBER_NAMES[number] + " hundred" + soFar;
    }

    public static String toEnglish(long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        String stringNumber = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(stringNumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(stringNumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(stringNumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(stringNumber.substring(9, 12));

        String result = billions == 0 ? "" : toEnglishLessThanOneThousand(billions) + " billion ";
        result += millions == 0 ? "" : toEnglishLessThanOneThousand(millions) + " million ";

        result += switch (hundredThousands) {
            case 0 -> "";
            case 1 -> "one thousand ";
            default -> toEnglishLessThanOneThousand(hundredThousands)
                    + " thousand ";
        };

        String tradThousand;
        tradThousand = toEnglishLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    public static String toRoman(int number) {
        StringBuilder roman = new StringBuilder();

        if (number < 0) {
            number = -number;
            roman.append('-');
        }

        int milliards = number / 1000;
        if (milliards > 20) {
            roman.append(milliards).append("xM+");
        } else {
            roman.append("M".repeat(Math.max(0, milliards)));
        }

        number %= 1000;

        for (int i = 0; i < ROMAN_NUMBERS_VALUES.length; i++) {
            while (number >= ROMAN_NUMBERS_VALUES[i]) {
                roman.append(ROMAN_NUMBERS[i]);
                number -= ROMAN_NUMBERS_VALUES[i];
            }
        }
        return roman.toString();
    }

    public static int swapBits(int n) {
        return swapBitsInByte(n >>> 24) << 24
                | swapBitsInByte(n >>> 16) << 16
                | swapBitsInByte(n >>> 8) << 8
                | swapBitsInByte(n);
    }

    public static int swapBitsInByte(int n) {
        int result = 0;
        for (int i = 0; i < 7; i++) {
            result += n & 1;
            n >>>= 1;
            result <<= 1;
        }
        result += n & 1;
        return result;
    }

    public static int crc32(int crc, int message, int bytes, int poly) {
        int mask = switch (bytes) {
            case 2 -> 0xFFFF;
            case 1 -> 0xFF;
            default -> 0xFFFFFFFF;
        };
        crc = crc ^ (message & mask);
        for (int i = 0; i < bytes << 3; i++) {
            int bitMask = -(crc & 1);
            crc = (crc >> 1) ^ (poly & bitMask);
        }
        return ~crc;
    }
}
