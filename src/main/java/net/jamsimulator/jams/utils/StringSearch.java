/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

import javafx.util.Pair;

import java.util.*;

public class StringSearch {

    private static final String[] SPLIT_VALUES = {"-", "_"};

    public static List<Result> search(String search, List<String> values) {
        var searchValuesRaw = StringUtils.splitCamelCaseWithIndex(search, false);
        var searchValues = new TreeMap<Integer, String>();

        searchValuesRaw.forEach((index, string) -> {
            var parsed = StringUtils.multiSplitIgnoreInsideStringWithIndex(string, false, SPLIT_VALUES);
            parsed.forEach((subIndex, substring) -> searchValues.put(index + subIndex, substring));
        });

        return values.stream().map(it -> search(searchValues, it)).toList();
    }

    private static Result search(SortedMap<Integer, String> searchValues, String value) {
        var ranges = new ArrayList<Pair<Integer, Integer>>();
        var lowerValue = value.toLowerCase(Locale.ROOT);

        int priority = 0;
        int start = 0;

        for (var entry : searchValues.entrySet()) {
            int matchIndex = lowerValue.indexOf(entry.getValue(), start);
            if (matchIndex == -1) return new Result(false, Collections.emptyList(), Integer.MAX_VALUE);

            int length = entry.getValue().length();
            ranges.add(new Pair<>(matchIndex, length));

            priority += matchIndex;
            start = matchIndex + length;
        }

        return new Result(true, ranges, priority);
    }

    public record Result(boolean found, List<Pair<Integer, Integer>> ranges, int priority) {
    }

}
