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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {

    public static List<String> multiSplit(String string, String... separators) {
        List<String> list = new ArrayList<>();
        int length = string.length(), from = 0;

        for (int i = 0; i < length; i++) {
            for (String separator : separators) {
                int sepLen = separator.length();
                if (string.regionMatches(i, separator, 0, sepLen)) {
                    list.add(string.substring(from, i));
                    from = i + sepLen;
                    i = from - 1;
                    break;
                }
            }
        }

        if (from <= length) list.add(string.substring(from));
        else list.add("");
        return list;
    }

    public static List<String> multiSplitIgnoreInsideString(String string, boolean addEmpty, String... separators) {
        List<String> list = new ArrayList<>();
        int length = string.length(), from = 0;

        boolean insideString = false;
        boolean insideChar = false;
        boolean escape = false;
        String result;
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c == '"' && !escape)
                insideString = !insideString;
            if (c == '\'' && !escape)
                insideChar = !insideChar;
            escape = !escape && c == '\\';

            if (insideString || insideChar) continue;
            for (String separator : separators) {
                int sepLen = separator.length();
                if (string.regionMatches(i, separator, 0, sepLen)) {
                    result = string.substring(from, i);
                    if (addEmpty || !result.isEmpty())
                        list.add(result);
                    from = i + sepLen;
                    i = from - 1;
                    break;
                }
            }
        }

        if (from < length) list.add(string.substring(from));
        else if (addEmpty) list.add("");
        return list;
    }

    public static Map<Integer, String> multiSplitIgnoreInsideStringWithIndex(String string, boolean addEmpty, String... separators) {
        Map<Integer, String> map = new HashMap<>();
        int length = string.length(), from = 0;

        boolean insideString = false;
        boolean insideChar = false;
        boolean escape = false;
        String result;
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c == '"' && !escape)
                insideString = !insideString;
            if (c == '\'' && !escape)
                insideChar = !insideChar;
            escape = !escape && c == '\\';

            if (insideString || insideChar) continue;
            for (String separator : separators) {
                int sepLen = separator.length();
                if (string.regionMatches(i, separator, 0, sepLen)) {
                    result = string.substring(from, i);
                    if (addEmpty || !result.isEmpty())
                        map.put(from, result);
                    from = i + sepLen;
                    i = from - 1;
                    break;
                }
            }
        }

        if (from < length) map.put(from, string.substring(from));
        else if (addEmpty) map.put(length, "");
        return map;
    }

    public static List<String> splitCamelCase(String string) {
        List<String> list = new ArrayList<>();
        int length = string.length(), from = 0;

        for (int i = 1; i < length; i++) {
            if (Character.isUpperCase(string.charAt(1))) {
                list.add(string.substring(from, i).toLowerCase(Locale.ROOT));
                from = i;
            }
        }

        list.add(string.substring(from).toLowerCase(Locale.ROOT));
        return list;
    }

    public static Map<Integer, String> splitCamelCaseWithIndex(String string, boolean addEmpty) {
        var map = new HashMap<Integer, String>();
        int length = string.length(), from = 0;

        for (int i = 1; i < length; i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                var splitted = string.substring(from, i);
                if (!splitted.isEmpty() || addEmpty) {
                    map.put(from, splitted.toLowerCase(Locale.ROOT));
                    from = i;
                }
            }
        }

        var splitted = string.substring(from);
        if (!splitted.isEmpty() || addEmpty) {
            map.put(from, splitted.toLowerCase(Locale.ROOT));
        }
        return map;
    }

    public static int indexOf(String string, char... chars) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            for (char match : chars) {
                if (array[i] == match) return i;
            }
        }
        return -1;
    }

    public static int indexOf(String string, int start, char... chars) {
        char[] array = string.toCharArray();
        for (int i = start; i < array.length; i++) {
            for (char match : chars) {
                if (array[i] == match) return i;
            }
        }
        return -1;
    }

    public static int getCommentIndex(String string) {
        char c;
        boolean insideString = false;
        boolean insideChar = false;
        boolean escape = true;
        for (int i = 0; i < string.length(); i++) {
            c = string.charAt(i);
            if (c == '"' && !escape) insideString = !insideString;
            if (c == '\'' && !escape) insideChar = !insideChar;
            if ((c == '#' || c == ';') && !insideString && !insideChar) return i;
            escape = !escape && c == '\\';
        }
        return -1;
    }

    public static String removeComments(String string) {
        char c;
        boolean insideString = false;
        boolean insideChar = false;
        boolean escape = true;
        for (int i = 0; i < string.length(); i++) {
            c = string.charAt(i);
            if (c == '"' && !escape) insideString = !insideString;
            if (c == '\'' && !escape) insideChar = !insideChar;
            if ((c == '#' || c == ';') && !insideString && !insideChar) return string.substring(0, i);
            escape = !escape && c == '\\';
        }
        return string;
    }

    public static boolean isStringOrChar(String string) {
        return string.startsWith("\"") && string.endsWith("\"") ||
                string.startsWith("'") && string.endsWith("'");
    }

    public static int charCount(String string, char... chars) {
        char[] array = string.toCharArray();
        int amount = 0;
        for (char current : array) {
            for (char target : chars) {
                if (target == current) {
                    amount++;
                    break;
                }
            }
        }
        return amount;
    }


    public static String addExtraSpaces(String string) {
        StringBuilder builder = new StringBuilder(string);
        while (builder.length() < 10) builder.append(" ");
        return builder.toString();
    }

    public static String addZeros(String s, int to) {
        StringBuilder builder = new StringBuilder();
        int max = Math.max(0, to - s.length());

        builder.append("0".repeat(max));

        return builder + s;
    }

    public static String addSpaces(String s, int to, boolean end) {
        String builder;
        int max = Math.max(0, to - s.length());

        builder = IntStream.range(0, max).mapToObj(i -> " ").collect(Collectors.joining());

        return end ? s + builder : builder + s;
    }

    public static String parseEscapeCharacters(String string) {
        boolean escaping = false;

        char[] numberBuffer = new char[3];
        int numberBufferIndex = 0;

        char[] utfBuffer = new char[4];
        int utfBufferIndex = 0;
        boolean utf = false;

        StringBuilder result = new StringBuilder();

        for (char c : string.toCharArray()) {
            //\XXX parse
            if (numberBufferIndex > 0) {

                if (c >= '0' && c <= '9') {
                    numberBuffer[numberBufferIndex++] = c;
                    if (numberBufferIndex == 3) {
                        numberBufferIndex = 0;
                        int number = Integer.parseInt(new String(numberBuffer));
                        if (number < 377) {
                            result.append((char) number);
                            continue;
                        }
                    } else continue;
                }

                result.append('\\');
                for (int i = 0; i < numberBufferIndex; i++) {
                    result.append(numberBuffer[i]);
                }
                numberBufferIndex = 0;

            }
            //UTF parse
            else if (utf) {
                if (c >= '0' && c <= '9'
                        || c >= 'A' && c <= 'F'
                        || c >= 'a' && c <= 'f') {
                    utfBuffer[utfBufferIndex++] = c;
                    if (utfBufferIndex == 4) {
                        utf = false;
                        utfBufferIndex = 0;
                        int number = NumericUtils.decodeInteger("0x" + new String(utfBuffer));
                        result.append(new String(new int[]{number}, 0, 1));
                    }
                    continue;
                }

                result.append("\\u");
                for (int i = 0; i < utfBufferIndex; i++) {
                    result.append(utfBuffer[i]);
                }
                utfBufferIndex = 0;
                utf = false;
            }
            //Escape parse
            else if (escaping) {
                escaping = false;
                switch (c) {
                    case 'b' -> result.append('\b');
                    case 'n' -> result.append('\n');
                    case 't' -> result.append('\t');
                    case 'r' -> result.append('\r');
                    case 'f' -> result.append('\f');
                    case '\'' -> result.append('\'');
                    case '\"' -> result.append('\"');
                    case '\\' -> result.append('\\');
                    case '0', '1', '2', '3' -> {
                        numberBuffer[0] = c;
                        numberBufferIndex = 1;
                    }
                    case 'u' -> utf = true;
                    default -> {
                        result.append('\\');
                        result.append(c);
                    }
                }
            }
            //Other
            else {
                if (c == '\\') escaping = true;
                else result.append(c);
            }
        }

        return result.toString();
    }

    public static String addLineJumps(String string, int maxCharsPerLine) {
        StringBuilder builder = new StringBuilder();
        StringBuilder wordBuilder = new StringBuilder();
        int count = 0;

        for (char c : string.toCharArray()) {
            if (c == ' ') {
                if (count >= maxCharsPerLine) {
                    builder.append(wordBuilder);
                    builder.append('\n');
                    wordBuilder = new StringBuilder();
                    count = 0;
                } else {
                    wordBuilder.append(c);
                    count++;
                }
            } else if (c == '\r' || c == '\n') {
                builder.append(wordBuilder);
                builder.append(c);
                wordBuilder = new StringBuilder();
                count = 0;
            } else {
                wordBuilder.append(c);
                count++;
            }
        }

        if (wordBuilder.length() > 0) {
            builder.append(wordBuilder);
        }

        return builder.toString();
    }

    public static int presentTimes(String string, String replacement) {
        int from = 0;
        int times = 0;

        while ((from = string.indexOf(replacement, from)) != -1) {
            from++;
            times++;
        }

        return times;
    }
}
